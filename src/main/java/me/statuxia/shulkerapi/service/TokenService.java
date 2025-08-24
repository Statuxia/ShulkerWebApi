package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.Account;

public interface TokenService {

    String createSessionToken(Account account);
}
