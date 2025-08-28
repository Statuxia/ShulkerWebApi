package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.DiscordAccount;

public interface AccountCreateService {

    Account create(DiscordAccount discordAccount);
}
