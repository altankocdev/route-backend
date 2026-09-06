package com.routeapp.routebackend.user.repository;

import com.routeapp.routebackend.user.entity.token.TokenType;
import com.routeapp.routebackend.user.entity.token.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {
    Optional<UserToken> findByTokenAndTokenType(String token, TokenType tokenType);
}