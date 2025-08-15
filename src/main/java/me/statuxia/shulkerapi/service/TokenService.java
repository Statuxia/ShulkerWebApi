package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.DiscordAccount;

public interface TokenService {

    String createSessionToken(DiscordAccount discordAccount);
}
