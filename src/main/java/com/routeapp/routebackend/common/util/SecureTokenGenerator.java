package com.routeapp.routebackend.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class SecureTokenGenerator {

    private SecureTokenGenerator() {
    }

    public static String generate() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}