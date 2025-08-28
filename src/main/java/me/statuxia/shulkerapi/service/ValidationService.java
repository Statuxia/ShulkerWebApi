package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.DiscordAccount;

public interface ValidationService {

    void validateAccount(Account account);

    void validateDiscordAccount(DiscordAccount discordAccount);
}
