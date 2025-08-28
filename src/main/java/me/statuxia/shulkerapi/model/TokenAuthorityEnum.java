package me.statuxia.shulkerapi.model;

import java.util.Arrays;
import java.util.List;

import static me.statuxia.shulkerapi.model.TokenAuthorityGroup.*;

public enum TokenAuthorityEnum {

    ADD_GAME_ACCOUNTS(ADMIN, PLUGIN),

    DEPOSIT_FUNDS_TO_CARD(PLUGIN),
    WITHDRAW_FUNDS_FROM_CARD(PLUGIN),
    CREATE_BANK_CARD(ADMIN),
    UPDATE_PIN_CODE(ADMIN),
    DISABLE_BANK_CARD(ADMIN),
    ENABLE_BANK_CARD(ADMIN),

    CREATE_CUSTOM_TOKENS(ADMIN);

    private final List<TokenAuthorityGroup> groups;

    TokenAuthorityEnum(TokenAuthorityGroup... groups) {
        this.groups = Arrays.stream(groups).toList();
    }

    TokenAuthorityEnum() {
        this.groups = List.of();
    }

    TokenAuthorityEnum(List<TokenAuthorityGroup> groups) {
        this.groups = groups;
    }

    public List<TokenAuthorityGroup> getGroups() {
        return groups;
    }
}
