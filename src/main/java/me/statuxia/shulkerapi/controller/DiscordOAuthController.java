package me.statuxia.shulkerapi.controller;

import jakarta.servlet.http.HttpServletResponse;
import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.provider.DiscordOAuthRedirectProvider;
import me.statuxia.shulkerapi.response.DiscordAccessTokenResponse;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;
import me.statuxia.shulkerapi.service.impl.DiscordIntegrationServiceImpl;
import me.statuxia.shulkerapi.utils.TokenGenerator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/oauth2/discord")
public class DiscordOAuthController extends BaseDiscordApiController {

    private final DiscordOAuthRedirectProvider provider;
    private final DiscordAccountDAO discordAccountDAO;

    @Autowired
    public DiscordOAuthController(
        DiscordIntegrationServiceImpl discordIntegrationService,
        DiscordOAuthRedirectProvider provider,
        DiscordAccountDAO discordAccountDAO
    ) {
        super(discordIntegrationService);
        this.provider = provider;
        this.discordAccountDAO = discordAccountDAO;
    }

    @GetMapping("/redirect")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect(getProvider().getDiscordRedirectUrl());
    }

    @GetMapping("/auth")
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

        if (userInfo.getBody() == null) {
            response.sendRedirect(getProvider().getAuthFailureRedirectUrl());
            return;
        }

        final Long discordId = userInfo.getBody().getId();
        final DiscordAccount account = getDiscordAccountDAO().findById(discordId).orElse(new DiscordAccount());
        account.setId(discordId);
        account.setAccessToken(tokenResponse.getAccessToken());
        account.setRefreshToken(tokenResponse.getRefreshToken());
        account.setUpdateTime(DateTime.now().plusSeconds(tokenResponse.getExpireIn().intValue()));
        account.setSessionToken(TokenGenerator.generate());

        getDiscordAccountDAO().save(account);

        response.sendRedirect(getProvider().getAuthSuccessRedirectUrl() + "?token=" + account.getSessionToken());
    }

    public DiscordOAuthRedirectProvider getProvider() {
        return provider;
    }

    public DiscordAccountDAO getDiscordAccountDAO() {
        return discordAccountDAO;
    }
}
