package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.response.DiscordAccessTokenResponse;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;

public interface DiscordAccountService {

    DiscordAccount createOrUpdate(Long discordId, DiscordAccessTokenResponse tokenResponse);

    DiscordAccount createOrGet(Long discordId);

    DiscordIdentityResponse getByDiscordId(Long discordId);

    DiscordIdentityResponse getByDiscord(DiscordAccount discordAccount);
}
