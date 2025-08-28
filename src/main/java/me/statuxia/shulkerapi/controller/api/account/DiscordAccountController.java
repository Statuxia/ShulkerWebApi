package me.statuxia.shulkerapi.controller.api.account;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.controller.api.AuthController;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.exception.AuthenticationException;
import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;
import me.statuxia.shulkerapi.service.DiscordIntegrationService;
import me.statuxia.shulkerapi.swagger.IncorrectDataOperation;
import me.statuxia.shulkerapi.swagger.NoDataOperation;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.DiscordAccountControllerOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static me.statuxia.shulkerapi.exception.BaseApiException.NO_DATA;

@RestController
@RequestMapping(value = DiscordAccountController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Tag(name = "Discord Profile", description = "Контроллер для получения авторизованных Discord профилей")
public class DiscordAccountController extends BaseDiscordApiController implements AuthController {

    public static final String PREFIX = "/api/v1/discord";
    public static final String GET = "/get";
    public static final String ID_PATH = "/{id}";

    private final DiscordAccountDAO discordAccountDAO;

    @Autowired
    public DiscordAccountController(
        DiscordIntegrationService discordIntegrationService,
        DiscordAccountDAO discordAccountDAO
    ) {
        super(discordIntegrationService);
        this.discordAccountDAO = discordAccountDAO;
    }

    @GetMapping(value = {GET + ID_PATH, GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    @UnknownAccountOperation
    @NoDataOperation
    @IncorrectDataOperation
    @DiscordAccountControllerOperation.Get
    public ResponseEntity<DiscordIdentityResponse> get(
        @PathVariable(value = "id", required = false) Long id,
        @AuthData TokenData token
    ) {
        if (id != null) {
            return findById(id, token);
        }

        return getResponse(token.getDiscordAccount(), false);
    }

    private ResponseEntity<DiscordIdentityResponse> findById(Long id, TokenData token) {
        final Optional<DiscordAccount> discordAccount = getDiscordAccountDAO().findById(id);
        if (discordAccount.isEmpty()) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        return getResponse(discordAccount.get(), true);
    }

    private @NotNull ResponseEntity<DiscordIdentityResponse> getResponse(
        DiscordAccount discordAccount, boolean byId
    ) {
        final String accessToken = discordAccount.getAccessToken();
        final HttpHandler.HttpResponse<DiscordIdentityResponse> response
            = getDiscordIntegrationService().getUserInfo(accessToken);
        if (response.getException() != null) {
            logger.error("error occured", response.getException());
            throw byId ? AccountException.UNKNOWN_ACCOUNT : AuthenticationException.DISCORD_ACCOUNT_EXPIRED;
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
