package com.routeapp.routebackend.auth.service;

public record GoogleUserInfo(String googleId,
                             String email,
                             String firstName,
                             String lastName,
                             String profilePhotoUrl) {}