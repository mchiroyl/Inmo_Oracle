package com.inmo.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static boolean matches(String raw, String hash) {
        if (raw == null || hash == null || hash.isBlank()) return false;
        return BCrypt.checkpw(raw, hash);
    }

    public static String hash(String raw) {
        if (raw == null) return null;
        return BCrypt.hashpw(raw, BCrypt.gensalt(10));
    }
}
