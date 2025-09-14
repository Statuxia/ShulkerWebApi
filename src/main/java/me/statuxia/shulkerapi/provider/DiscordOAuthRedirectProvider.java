package me.statuxia.shulkerapi.provider;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import me.statuxia.shulkerapi.configuration.properties.DiscordOAuthProperties;
import me.statuxia.shulkerapi.service.impl.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class DiscordOAuthRedirectProvider {

    public static final String X_REDIRECT_HEADER = "X-Redirect";

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

    public String getAuthFailureRedirectUrl(HttpServletRequest request) {
        return getRedirect(request, authFailureRedirectUrl);
    }

    public String getAuthSuccessRedirectUrl(HttpServletRequest request) {
        return getRedirect(request, authSuccessRedirectUrl);
    }

    public String getRedirect(HttpServletRequest request, String defaultRedirect) {
        final String header = request.getHeader(X_REDIRECT_HEADER);
        return StringUtils.hasText(header) ? header : defaultRedirect;
    }
}
