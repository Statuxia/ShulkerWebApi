package me.statuxia.shulkerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.provider.DiscordOAuthRedirectProvider;
import me.statuxia.shulkerapi.response.DiscordAccessTokenResponse;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;
import me.statuxia.shulkerapi.service.DiscordAccountService;
import me.statuxia.shulkerapi.service.DiscordIntegrationService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(DiscordOAuthController.PREFIX)
@Tag(name = "Discord OAuth", description = "Контроллер для аутентификации через Discord")
public class DiscordOAuthController extends BaseDiscordApiController {

    public static final String PREFIX = "/api/v1/oauth2/discord";
    public static final String REDIRECT = "/redirect";
    public static final String AUTH = "/auth";

    private final DiscordOAuthRedirectProvider provider;
    private final DiscordAccountService discordAccountService;

    public DiscordOAuthController(
        DiscordIntegrationService discordIntegrationService,
        DiscordOAuthRedirectProvider provider,
        DiscordAccountService discordAccountService
    ) {
        super(discordIntegrationService);
        this.provider = provider;
        this.discordAccountService = discordAccountService;
    }

    @GetMapping(REDIRECT)
    @Operation(description = "Эндпоинт для простого редиректа в сервис Discord OAuth")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect(getProvider().getDiscordRedirectUrl());
    }

    @GetMapping(AUTH)
    @Transactional
    @Operation(description = """
        Эндпоинт для авторизации полученного от Discord OAuth кода с последующим редиректом в лк
        """)
    public void auth(HttpServletResponse response, @RequestParam("code") String code) throws IOException {
        final HttpHandler.HttpResponse<DiscordAccessTokenResponse> accessToken
            = getDiscordIntegrationService().getAccessToken(code);
        if (accessToken.getException() != null) {
            logger.error("error occured", accessToken.getException());
            response.sendRedirect(getProvider().getAuthFailureRedirectUrl());
            return;
        }

        final DiscordAccessTokenResponse tokenResponse = accessToken.getBody();
        if (tokenResponse == null) {
            response.sendRedirect(getProvider().getAuthFailureRedirectUrl());
            return;
        }

        final HttpHandler.HttpResponse<DiscordIdentityResponse> userInfo
            = getDiscordIntegrationService().getUserInfo(tokenResponse.getAccessToken());
        if (userInfo.getException() != null) {
            logger.error("error occured", userInfo.getException());
            response.sendRedirect(getProvider().getAuthFailureRedirectUrl());
            return;
        }

        final DiscordIdentityResponse userIdentify = userInfo.getBody();
        if (userIdentify == null) {
            response.sendRedirect(getProvider().getAuthFailureRedirectUrl());
            return;
        }

        final DiscordAccount account = getDiscordAccountService().createOrUpdate(userIdentify.getId(), tokenResponse);

        response.sendRedirect(getProvider().getAuthSuccessRedirectUrl() + "?token=" + account.getSessionToken());
    }

    public DiscordOAuthRedirectProvider getProvider() {
        return provider;
    }

    public DiscordAccountService getDiscordAccountService() {
        return discordAccountService;
    }
}
