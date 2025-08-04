package me.statuxia.shulkerapi.scheduler;

import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.response.DiscordAccessTokenResponse;
import me.statuxia.shulkerapi.service.impl.DiscordIntegrationServiceImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DiscordOAuthTokenUpdate {

    public static final int BATCH_SIZE = 10;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DiscordAccountDAO discordAccountDAO;
    private final DiscordIntegrationServiceImpl discordIntegrationService;

    @Autowired
    public DiscordOAuthTokenUpdate(
        DiscordIntegrationServiceImpl discordIntegrationService, DiscordAccountDAO discordAccountDAO) {
        this.discordIntegrationService = discordIntegrationService;
        this.discordAccountDAO = discordAccountDAO;
    }

    @Scheduled(initialDelay = 10, fixedRate = 15 * 60, timeUnit = TimeUnit.SECONDS)
    public void process() {
        final DateTime dateTime = DateTime.now().plusDays(1);
        final long total = getDiscordAccountDAO().countByUpdateTimeLessThanEqual(dateTime);

        final List<DiscordAccount> needToUpdateAccounts = getDiscordAccountDAO().findByUpdateTimeLessThanEqual(
            dateTime, Pageable.ofSize(BATCH_SIZE)
        );

        logger.debug("process {}/{} need to update accounts", needToUpdateAccounts.size(), total);

        int updated = 0;
        int errors = 0;
        for (DiscordAccount account : needToUpdateAccounts) {
            final HttpHandler.HttpResponse<DiscordAccessTokenResponse> response
                = getDiscordIntegrationService().refreshToken(account.getRefreshToken());
            if (response.getException() != null) {
                logger.error("caught error on updating account {}", account.getId());
                errors++;
                continue;
            }

            final DiscordAccessTokenResponse responseBody = response.getBody();
            if (responseBody == null) {
                logger.error("body is null for account {}", account.getId());
                errors++;
                continue;
            }

            account.setUpdateTime(DateTime.now().plusSeconds(responseBody.getExpireIn().intValue()));
            account.setAccessToken(responseBody.getAccessToken());
            account.setRefreshToken(responseBody.getRefreshToken());
            updated++;
        }

        logger.debug("updated: {} errors: {}", updated, errors);
        getDiscordAccountDAO().saveAll(needToUpdateAccounts);
    }

    public DiscordIntegrationServiceImpl getDiscordIntegrationService() {
        return discordIntegrationService;
    }

    public DiscordAccountDAO getDiscordAccountDAO() {
        return discordAccountDAO;
    }
}
