package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;

public interface TokenService {

    String createSessionToken(Account account);

    boolean hasAuthority(String token, TokenAuthorityEnum authority);
}
