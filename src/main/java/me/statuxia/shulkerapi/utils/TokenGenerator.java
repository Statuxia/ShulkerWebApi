package me.statuxia.shulkerapi.utils;

import java.security.SecureRandom;

public class TokenGenerator {

    public static final int TOKEN_LENGTH = 24;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private TokenGenerator() {
    }

    public static String generate() {
        return generate(TOKEN_LENGTH);
    }

    public static String generate(int tokenLength) {
        if (tokenLength < TOKEN_LENGTH) {
            throw new IllegalArgumentException("token should not be less than " + TOKEN_LENGTH);
        }

        final StringBuilder token = new StringBuilder(tokenLength);
        for (int i = 0; i < tokenLength; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            token.append(CHARACTERS.charAt(index));
        }
        return token.toString();
    }
}
