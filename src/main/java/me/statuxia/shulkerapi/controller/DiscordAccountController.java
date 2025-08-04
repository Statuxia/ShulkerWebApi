package me.statuxia.shulkerapi.controller;

import me.statuxia.shulkerapi.annotations.TokenData;
import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.Token;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;
import me.statuxia.shulkerapi.service.impl.DiscordIntegrationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/discord")
public class DiscordAccountController extends BaseDiscordApiController {

    private final DiscordAccountDAO discordAccountDAO;

    @Autowired
    public DiscordAccountController(
        DiscordIntegrationServiceImpl discordIntegrationService,
        DiscordAccountDAO discordAccountDAO
    ) {
        super(discordIntegrationService);
        this.discordAccountDAO = discordAccountDAO;
    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiscordIdentityResponse> get(@TokenData Token token) {
        final Optional<DiscordAccount> discordAccount = getDiscordAccountDAO().findBySessionToken(token.sessionToken());
        if (discordAccount.isEmpty()) {
            throw new BadCredentialsException("know nothing about this token");
        }

        final String accessToken = discordAccount.get().getAccessToken();
        final HttpHandler.HttpResponse<DiscordIdentityResponse> response
            = getDiscordIntegrationService().getUserInfo(accessToken);
        if (response.getException() != null) {
            logger.error("error occured", response.getException());
            throw new AccountExpiredException("dead access token");
        }

        if (response.getBody() == null) {
            throw new BadCredentialsException("know nothing about this account");
        }

        return ResponseEntity.ok(response.getBody());
    }

    public DiscordAccountDAO getDiscordAccountDAO() {
        return discordAccountDAO;
    }
}
