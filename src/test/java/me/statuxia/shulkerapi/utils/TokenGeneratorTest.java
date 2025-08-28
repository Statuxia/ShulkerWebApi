package me.statuxia.shulkerapi.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenGeneratorTest {

    @Test
    void generateTest() {
        final String token = TokenGenerator.generate();
        assertTrue(StringUtils.hasText(token));
        assertEquals(TokenGenerator.TOKEN_LENGTH, token.length());
    }

    @Test
    void generateCustomLengthTest() {
        final String token = TokenGenerator.generate(64);
        assertTrue(StringUtils.hasText(token));
        assertEquals(64, token.length());
    }

    @Test
    void generateIncorrectLengthTest() {
        assertThrows(
            IllegalArgumentException.class,
            () -> TokenGenerator.generate(TokenGenerator.TOKEN_LENGTH - 1)
        );
    }

}