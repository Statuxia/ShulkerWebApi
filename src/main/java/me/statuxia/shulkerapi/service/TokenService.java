package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;

public interface TokenService {

    String createSessionToken(DiscordAccount discordAccount);

    boolean hasAuthority(String token, TokenAuthorityEnum authority);
}
