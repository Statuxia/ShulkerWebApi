package me.statuxia.shulkerapi.controller;

import me.statuxia.shulkerapi.service.impl.DiscordIntegrationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseDiscordApiController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final DiscordIntegrationServiceImpl discordIntegrationService;

    @Autowired
    protected BaseDiscordApiController(DiscordIntegrationServiceImpl discordIntegrationService) {
        this.discordIntegrationService = discordIntegrationService;
    }

    public DiscordIntegrationServiceImpl getDiscordIntegrationService() {
        return discordIntegrationService;
    }
}
