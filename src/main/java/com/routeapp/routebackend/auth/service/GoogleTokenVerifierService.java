package com.routeapp.routebackend.auth.service;

public interface GoogleTokenVerifierService {
    GoogleUserInfo verify(String idToken);
}