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

}