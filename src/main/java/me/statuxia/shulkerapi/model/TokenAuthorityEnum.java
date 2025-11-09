package me.statuxia.shulkerapi.model;

import java.util.Arrays;
import java.util.List;

import static me.statuxia.shulkerapi.model.TokenAuthorityGroup.*;

public enum TokenAuthorityEnum {

    ADD_GAME_ACCOUNTS(ADMIN, PLUGIN),
    CAN_CREATE_TWINK(ADMIN, PLUGIN),
    RENAME_GAME_ACCOUNTS(ADMIN, PLUGIN),

    AUTH_VALIDATE(ADMIN, PLUGIN),
    AUTH_QUEUE(ADMIN, DISCORD_BOT),
    AUTH_CHANGE_STATE(ADMIN, DISCORD_BOT),
    AUTH_REFRESH(ADMIN, PLUGIN),

    DEPOSIT_FUNDS_TO_CARD(PLUGIN),
    WITHDRAW_FUNDS_FROM_CARD(PLUGIN),
    TRANSFER_FUNDS_FROM_CARD(PLUGIN),
    GET_CARD_WITHOUT_REMOVE_DATA(PLUGIN),
    CREATE_BANK_CARD(ADMIN),
    UPDATE_PIN_CODE(ADMIN),
    DISABLE_BANK_CARD(ADMIN),
    ENABLE_BANK_CARD(ADMIN),
    LIST_BANK_CARD(PLUGIN),
    LIST_BANK_CARD_HISTORY(PLUGIN),
    HISTORY_ROLLBACK_OPERATION(PLUGIN),
    HISTORY_RESTORE_OPERATION(PLUGIN),
    CREATE_FINE(ADMIN),
    EDIT_MESSAGE_FINE(ADMIN),
    CLOSE_FINE(ADMIN),
    LIST_FINE(ADMIN),
    PAY_FINE(ADMIN),
    ADMIN_CARD_GET(ADMIN),
    ADMIN_CARD_WITHDRAW(ADMIN),

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
