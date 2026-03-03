package me.statuxia.shulkerapi.controller.api.account;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.controller.api.Controller;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.exception.AuthenticationException;
import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.DiscordAccountLinkRequest;
import me.statuxia.shulkerapi.response.DiscordAccountUnlinkResponse;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;
import me.statuxia.shulkerapi.service.DiscordAccountService;
import me.statuxia.shulkerapi.service.DiscordIntegrationService;
import me.statuxia.shulkerapi.swagger.IncorrectDataOperation;
import me.statuxia.shulkerapi.swagger.NoDataOperation;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.UnknownLinkAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.DiscordAccountControllerOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static me.statuxia.shulkerapi.exception.BaseApiException.NO_DATA;

@RestController
@RequestMapping(value = DiscordAccountController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Tag(name = "Discord Profile", description = "Контроллер для получения авторизованных Discord профилей")
public class DiscordAccountController extends BaseDiscordApiController implements Controller {

    public static final String PREFIX = "/api/v1/discord";
    public static final String GET = "/get";
    public static final String UNLINK = "/unlink";
    public static final String LINK = "/link";
    public static final String ID_PATH = "/{id}";
    public static final long ACCOUNT_LINK_STORAGE = 100000002L;

    private final DiscordAccountDAO discordAccountDAO;
    private final GameAccountDAO gameAccountDAO;
    private final DiscordAccountService discordAccountService;

    @Autowired
    public DiscordAccountController(
        DiscordIntegrationService discordIntegrationService,
        DiscordAccountDAO discordAccountDAO, GameAccountDAO gameAccountDAO, DiscordAccountService discordAccountService
    ) {
        super(discordIntegrationService);
        this.discordAccountDAO = discordAccountDAO;
        this.gameAccountDAO = gameAccountDAO;
        this.discordAccountService = discordAccountService;
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

    @RequiredAuthority(requireAll = TokenAuthorityEnum.UNLINK_ACCOUNT)
    @PostMapping(value = UNLINK, produces = MediaType.APPLICATION_JSON_VALUE)
    @UnknownAccountOperation
    @UnknownLinkAccountOperation
    @DiscordAccountControllerOperation.Unlink
    public ResponseEntity<DiscordAccountUnlinkResponse> unlink(
        @RequestBody DiscordAccountLinkRequest request,
        @AuthData TokenData token
    ) {
        final Optional<DiscordAccount> optDiscordAccount = getDiscordAccountDAO().findById(request.getDiscordId());
        if (optDiscordAccount.isEmpty()) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        if (request.getUsername().equalsIgnoreCase("Администратор")) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        final DiscordAccount discordAccount = optDiscordAccount.get();
        final DiscordAccount linkAccountStorage = getLinkAccountStorage();

        if (Objects.equals(discordAccount.getId(), linkAccountStorage.getId())) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        final List<GameAccount> list = gameAccountDAO.findByDiscordAccount(discordAccount).stream()
            .filter(gameAccount -> gameAccount.getName().equalsIgnoreCase(request.getUsername()))
            .toList();

        list.forEach(item -> item.setDiscordAccount(linkAccountStorage));
        gameAccountDAO.saveAll(list);

        final DiscordAccountUnlinkResponse response = new DiscordAccountUnlinkResponse();
        response.setUsernames(list.stream().map(GameAccount::getName).toList());
        return ResponseEntity.ok(response);
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.LINK_ACCOUNT)
    @PostMapping(value = LINK, produces = MediaType.APPLICATION_JSON_VALUE)
    @UnknownAccountOperation
    @UnknownLinkAccountOperation
    @DiscordAccountControllerOperation.Link
    public ResponseEntity<DiscordAccountUnlinkResponse> link(
        @RequestBody DiscordAccountLinkRequest request,
        @AuthData TokenData token
    ) {
        final DiscordAccount discordAccount = discordAccountService.createOrGet(request.getDiscordId());
        final DiscordAccount linkAccountStorage = getLinkAccountStorage();

        if (Objects.equals(discordAccount.getId(), linkAccountStorage.getId())) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        final List<GameAccount> list = gameAccountDAO.findByDiscordAccount(linkAccountStorage).stream()
            .filter(gameAccount -> gameAccount.getName().equalsIgnoreCase(request.getUsername()))
            .toList();

        final List<GameAccount> ownedAccounts = gameAccountDAO.findByDiscordAccount(discordAccount);
        final boolean hasMainAccount = ownedAccounts.stream().anyMatch(gameAccount -> !gameAccount.isPaid());

        for (GameAccount gameAccount : list) {
            if (gameAccount.isPaid() && !hasMainAccount) {
                throw AccountException.LINK_NO_MAIN_ACCOUNT;
            }
            if (!gameAccount.isPaid() && hasMainAccount) {
                throw AccountException.LINK_MAIN_ACCOUNT;
            }
        }

        list.forEach(item -> item.setDiscordAccount(discordAccount));
        gameAccountDAO.saveAll(list);

        final DiscordAccountUnlinkResponse response = new DiscordAccountUnlinkResponse();
        response.setUsernames(list.stream().map(GameAccount::getName).toList());
        return ResponseEntity.ok(response);
    }

    private DiscordAccount getLinkAccountStorage() {
        final Optional<DiscordAccount> linkAccountStorage = getDiscordAccountDAO().findById(ACCOUNT_LINK_STORAGE);
        if (linkAccountStorage.isEmpty()) {
            throw AccountException.UNKNOWN_LINK_ACCOUNT_STORAGE;
        }
        return linkAccountStorage.get();
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
