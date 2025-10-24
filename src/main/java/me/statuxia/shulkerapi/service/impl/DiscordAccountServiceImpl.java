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
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static me.statuxia.shulkerapi.exception.BaseApiException.NO_DATA;

@Service
@Transactional
public class DiscordAccountServiceImpl implements DiscordAccountService {

    private final DiscordAccountDAO discordAccountDAO;
    private final DiscordIntegrationService discordIntegrationService;
    private final AccountCreateService accountCreateService;
    private final DiscordAccountService discordAccountService;

    @Autowired
    public DiscordAccountServiceImpl(
        DiscordAccountDAO discordAccountDAO,
        DiscordIntegrationService discordIntegrationService,
        AccountCreateService accountCreateService
    ) {
        this.discordAccountDAO = discordAccountDAO;
        this.discordIntegrationService = discordIntegrationService;
        this.accountCreateService = accountCreateService;
        this.discordAccountService = this;
    }

    @Override
    @Transactional
    public DiscordAccount createOrUpdate(Long discordId, DiscordAccessTokenResponse tokenResponse) {
        final DiscordAccount discordAccount = getDiscordAccountDAO().findById(discordId).orElse(new DiscordAccount());
        discordAccount.setId(discordId);
        discordAccount.setAccessToken(tokenResponse.getAccessToken());
        discordAccount.setRefreshToken(tokenResponse.getRefreshToken());
        discordAccount.setUpdateTime(DateTime.now().plusSeconds(tokenResponse.getExpireIn().intValue()));

        getDiscordAccountDAO().save(discordAccount);
        getAccountCreateService().create(discordAccount);

        return discordAccount;
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

        getDiscordAccountDAO().save(account);
        getAccountCreateService().create(account);

        return account;
    }

    @Override
    public Optional<DiscordAccount> get(Long discordId) {
        return getDiscordAccountDAO().findById(discordId);
    }

    @Cacheable(value = "discord_identity", key = "#discordId")
    public DiscordIdentityResponse getByDiscordId(Long discordId) {
        final Optional<DiscordAccount> discordAccount = discordAccountDAO.findById(discordId);
        if (discordAccount.isEmpty()) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        return discordAccountService.getByDiscord(discordAccount.get());
    }

    @Cacheable(value = "discord_identity", key = "#discordAccount.id")
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
}
