package com.routeapp.routebackend.auth.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.routeapp.routebackend.auth.service.GoogleTokenVerifierService;
import com.routeapp.routebackend.auth.service.GoogleUserInfo;
import com.routeapp.routebackend.common.exception.BusinessException;
import com.routeapp.routebackend.common.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Service
public class GoogleTokenVerifierServiceImpl implements GoogleTokenVerifierService {

    @Value("${app.google.client-id}")
    private String googleClientId;

    private GoogleIdTokenVerifier verifier;

    @PostConstruct
    private void init() throws GeneralSecurityException, java.io.IOException {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    @Override
    public GoogleUserInfo verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new BusinessException(ErrorCode.GOOGLE_TOKEN_INVALID);
            }
            GoogleIdToken.Payload payload = idToken.getPayload();
            String fullName = (String) payload.get("name");
            String[] nameParts = splitName(fullName);

            return new GoogleUserInfo(
                    payload.getSubject(),
                    payload.getEmail(),
                    nameParts[0],
                    nameParts[1],
                    (String) payload.get("picture")
            );
        } catch (GeneralSecurityException | java.io.IOException e) {
            log.error("Google token doğrulama hatası: {}", e.getMessage());
            throw new BusinessException(ErrorCode.GOOGLE_TOKEN_INVALID);
        }
    }

    private String[] splitName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[]{"Google", "Kullanıcı"};
        }
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length == 2 ? parts : new String[]{parts[0], ""};
    }
}