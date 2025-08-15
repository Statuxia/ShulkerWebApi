package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.response.DiscordAccessTokenResponse;
import me.statuxia.shulkerapi.service.AccountCreateService;
import me.statuxia.shulkerapi.service.DiscordAccountService;
import me.statuxia.shulkerapi.service.TokenService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiscordAccountServiceImpl implements DiscordAccountService {

    private final DiscordAccountDAO discordAccountDAO;
    private final AccountCreateService accountCreateService;
    private final TokenService tokenService;

    @Autowired
    public DiscordAccountServiceImpl(
        DiscordAccountDAO discordAccountDAO,
        AccountCreateService accountCreateService,
        TokenService tokenService
    ) {
        this.discordAccountDAO = discordAccountDAO;
        this.accountCreateService = accountCreateService;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public DiscordAccount createOrUpdate(Long discordId, DiscordAccessTokenResponse tokenResponse) {
        final DiscordAccount account = getDiscordAccountDAO().findById(discordId).orElse(new DiscordAccount());
        account.setId(discordId);
        account.setAccessToken(tokenResponse.getAccessToken());
        account.setRefreshToken(tokenResponse.getRefreshToken());
        account.setUpdateTime(DateTime.now().plusSeconds(tokenResponse.getExpireIn().intValue()));

        final String sessionToken = getTokenLimitationService().createSessionToken(account);
        account.setSessionToken(sessionToken);

        getDiscordAccountDAO().save(account);
        getAccountCreateService().create(account);

        return account;
    }

    public DiscordAccountDAO getDiscordAccountDAO() {
        return discordAccountDAO;
    }

    public AccountCreateService getAccountCreateService() {
        return accountCreateService;
    }

    public TokenService getTokenLimitationService() {
        return tokenService;
    }
}
