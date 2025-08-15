package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.response.DiscordAccessTokenResponse;

public interface DiscordAccountService {

    DiscordAccount createOrUpdate(Long discordId, DiscordAccessTokenResponse tokenResponse);
}
