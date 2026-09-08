package com.routeapp.routebackend.auth.security;

import com.routeapp.routebackend.auth.entity.PrincipalType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration-ms}") long accessTokenExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public String generateAccessToken(UUID principalId, PrincipalType type) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(principalId.toString())
                .claim("type", type.name())
                .claim("authorities", type.toAuthority())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenExpirationMs)))
                .signWith(secretKey)
                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMs / 1000;
    }

    // Geçersiz/süresi dolmuş token'da null döner — filter bunu 401'e çevirecek.
    public AuthenticatedPrincipal parseAndValidate(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID id = UUID.fromString(claims.getSubject());
            PrincipalType type = PrincipalType.valueOf(claims.get("type", String.class));
            return new AuthenticatedPrincipal(id, type);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT doğrulama başarısız: {}", e.getMessage());
            return null;
        }
    }
}