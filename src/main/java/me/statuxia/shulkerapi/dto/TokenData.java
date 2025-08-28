package me.statuxia.shulkerapi.dto;

import io.swagger.v3.oas.annotations.Hidden;
import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.TokenSource;

/**
 * @param token  токен пользователя
 * @param source источник токена
 */
@Hidden
public record TokenData(
    String token,
    TokenSource source
) {

    public Account getAccount() {
        return source.getAccount();
    }

    public DiscordAccount getDiscordAccount() {
        return getAccount().getDiscordAccount();
    }
}
