package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.response.DiscordAccessTokenResponse;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;
import me.statuxia.shulkerapi.service.AccountCreateService;
import me.statuxia.shulkerapi.service.DiscordAccountService;
import me.statuxia.shulkerapi.service.DiscordIntegrationService;
import me.statuxia.shulkerapi.service.TokenService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static me.statuxia.shulkerapi.exception.BaseApiException.NO_DATA;

@Service
public class DiscordAccountServiceImpl implements DiscordAccountService {

    private final DiscordAccountDAO discordAccountDAO;
    private final DiscordIntegrationService discordIntegrationService;
    private final AccountCreateService accountCreateService;
    private final TokenService tokenService;

    @Autowired
    public DiscordAccountServiceImpl(
        DiscordAccountDAO discordAccountDAO, DiscordIntegrationService discordIntegrationService,
        AccountCreateService accountCreateService, TokenService tokenService
    ) {
        this.discordAccountDAO = discordAccountDAO;
        this.discordIntegrationService = discordIntegrationService;
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

    @Override
    public DiscordAccount createOrGet(Long discordId) {
        final Optional<DiscordAccount> discordAccount = getDiscordAccountDAO().findById(discordId);
        if (discordAccount.isPresent()) {
            return discordAccount.get();
        }

        final DiscordAccount account = new DiscordAccount();
        account.setId(discordId);
        account.setAccessToken("generated");
        account.setRefreshToken("generated");
        account.setUpdateTime(DateTime.now().plusYears(100));

        final String sessionToken = getTokenLimitationService().createSessionToken(account);
        account.setSessionToken(sessionToken);

        getDiscordAccountDAO().save(account);
        getAccountCreateService().create(account);

        return account;
    }

    public DiscordIdentityResponse getByDiscordId(Long discordId) {
        final Optional<DiscordAccount> discordAccount = discordAccountDAO.findById(discordId);
        if (discordAccount.isEmpty()) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        return getByDiscord(discordAccount.get());
    }

    public DiscordIdentityResponse getByDiscord(DiscordAccount discordAccount) {
        final String accessToken = discordAccount.getAccessToken();
        final HttpHandler.HttpResponse<DiscordIdentityResponse> response
            = discordIntegrationService.getUserInfo(accessToken);

        if (response.getException() != null) {
            final DiscordIdentityResponse identityResponse = new DiscordIdentityResponse();
            identityResponse.setId(discordAccount.getId());
            return identityResponse;
        }

        if (response.getBody() == null) {
            throw NO_DATA;
        }

        return response.getBody();
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
