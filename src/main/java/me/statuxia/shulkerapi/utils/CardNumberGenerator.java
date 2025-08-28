package me.statuxia.shulkerapi.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.StringJoiner;

public class CardNumberGenerator {

    public static final int CARD_PARTS = 2;
    public static final int CARD_PART_LENGTH = 4;

    private CardNumberGenerator() {
    }

    public static String generate() {
        final StringJoiner joiner = new StringJoiner(" ");
        for (int i = 0; i < CARD_PARTS; i++) {
            joiner.add(RandomStringUtils.secure().nextNumeric(CARD_PART_LENGTH));
        }

        return joiner.toString();
    }
}
