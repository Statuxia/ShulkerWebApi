package me.statuxia.shulkerapi.controller;

import me.statuxia.shulkerapi.annotations.TokenData;
import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.Token;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;
import me.statuxia.shulkerapi.service.DiscordIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static me.statuxia.shulkerapi.exception.AuthenticationException.DISCORD_ACCOUNT_EXPIRED;
import static me.statuxia.shulkerapi.exception.AuthenticationException.UNKNOWN_SESSION_TOKEN;
import static me.statuxia.shulkerapi.exception.DiscordIntegrationApiException.NO_DATA;

@RestController
@RequestMapping(DiscordAccountController.PREFIX)
public class DiscordAccountController extends BaseDiscordApiController {

    public static final String PREFIX = "/api/v1/discord";
    public static final String GET = "/get";

    private final DiscordAccountDAO discordAccountDAO;

    @Autowired
    public DiscordAccountController(
        DiscordIntegrationService discordIntegrationService,
        DiscordAccountDAO discordAccountDAO
    ) {
        super(discordIntegrationService);
        this.discordAccountDAO = discordAccountDAO;
    }

    @GetMapping(value = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiscordIdentityResponse> get(@TokenData Token token) {
        final Optional<DiscordAccount> discordAccount = getDiscordAccountDAO().findBySessionToken(token.sessionToken());
        if (discordAccount.isEmpty()) {
            throw UNKNOWN_SESSION_TOKEN;
        }

        final String accessToken = discordAccount.get().getAccessToken();
        final HttpHandler.HttpResponse<DiscordIdentityResponse> response
            = getDiscordIntegrationService().getUserInfo(accessToken);
        if (response.getException() != null) {
            logger.error("error occured", response.getException());
            throw DISCORD_ACCOUNT_EXPIRED;
        }

        if (response.getBody() == null) {
            throw NO_DATA;
        }

        return ResponseEntity.ok(response.getBody());
    }

    public DiscordAccountDAO getDiscordAccountDAO() {
        return discordAccountDAO;
    }
}
