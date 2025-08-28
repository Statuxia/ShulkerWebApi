package me.statuxia.shulkerapi.controller.api.account;

import me.statuxia.shulkerapi.service.DiscordIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseDiscordApiController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final DiscordIntegrationService discordIntegrationService;

    @Autowired
    protected BaseDiscordApiController(DiscordIntegrationService discordIntegrationService) {
        this.discordIntegrationService = discordIntegrationService;
    }

    public DiscordIntegrationService getDiscordIntegrationService() {
        return discordIntegrationService;
    }
}
