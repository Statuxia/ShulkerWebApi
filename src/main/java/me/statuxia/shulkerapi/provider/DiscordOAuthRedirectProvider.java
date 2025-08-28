package me.statuxia.shulkerapi.provider;

import jakarta.annotation.PostConstruct;
import me.statuxia.shulkerapi.configuration.properties.DiscordOAuthProperties;
import me.statuxia.shulkerapi.service.impl.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiscordOAuthRedirectProvider {

    private static final String DISCORD_REDIRECT_URL_KEY = "url.discord.redirect";

    private final MessageService messageService;
    private final DiscordOAuthProperties properties;

    private String discordRedirectUrl;
    @Value("${url.auth.success.redirect}")
    private String authSuccessRedirectUrl;
    @Value("${url.auth.failure.redirect}")
    private String authFailureRedirectUrl;

    @Autowired
    public DiscordOAuthRedirectProvider(MessageService messageService, DiscordOAuthProperties properties) {
        this.messageService = messageService;
        this.properties = properties;
    }

    @PostConstruct
    public void initRedirectUrl() {
        final String clientId = properties.getClientId();
        final String scopes = String.join(" ", properties.getScope());
        final String redirectUri = properties.getRedirectUri();

        discordRedirectUrl = messageService.message(DISCORD_REDIRECT_URL_KEY, List.of(clientId, redirectUri, scopes));
    }

    public String getDiscordRedirectUrl() {
        return discordRedirectUrl;
    }

    public String getAuthFailureRedirectUrl() {
        return authFailureRedirectUrl;
    }

    public String getAuthSuccessRedirectUrl() {
        return authSuccessRedirectUrl;
    }

}
