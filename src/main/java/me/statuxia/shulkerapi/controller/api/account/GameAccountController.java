package me.statuxia.shulkerapi.controller.api.account;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.controller.api.AuthController;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.GameAccountCreateRequest;
import me.statuxia.shulkerapi.request.GameAccountRenameRequest;
import me.statuxia.shulkerapi.response.GameAccountResponse;
import me.statuxia.shulkerapi.service.DiscordAccountService;
import me.statuxia.shulkerapi.swagger.AuthorityOperation;
import me.statuxia.shulkerapi.swagger.IncorrectDataOperation;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.GameAccountControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.account.NicknameAlreadyTakenOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = GameAccountController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Tag(name = "Game Profile", description = "Контроллер для создания и получения игровых профилей")
public class GameAccountController implements AuthController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String PREFIX = "/api/v1/game-account";
    public static final String CREATE = "/create";
    public static final String RENAME = "/rename";

    private final GameAccountDAO gameAccountDAO;
    private final DiscordAccountService discordAccountService;

    @Autowired
    public GameAccountController(GameAccountDAO gameAccountDAO, DiscordAccountService discordAccountService) {
        this.gameAccountDAO = gameAccountDAO;
        this.discordAccountService = discordAccountService;
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.ADD_GAME_ACCOUNTS)
    @PostMapping(value = CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @GameAccountControllerOperation.Create
    @AuthorityOperation
    @IncorrectDataOperation
    public ResponseEntity<GameAccountResponse> create(
        @RequestBody GameAccountCreateRequest request, @AuthData TokenData token
    ) {
        final String name = request.getName();
        if (!StringUtils.hasText(name) || request.getDiscordId() == null) {
            throw BaseApiException.INCORRECT_DATA;
        }

        final List<GameAccount> gameAccounts = new ArrayList<>(gameAccountDAO.findByNameIgnoreCase(name));
        if (gameAccounts.size() > 1) {
            logger.warn("{} accounts by name {}", gameAccounts.size(), name);
        }

        if (gameAccounts.isEmpty()) {
            final DiscordAccount discordAccount = discordAccountService.createOrGet(request.getDiscordId());

            final GameAccount account = new GameAccount();
            account.setName(name);
            account.setDiscordAccount(discordAccount);
            gameAccounts.add(account);
        }

        gameAccounts.forEach(account -> account.setName(name));
        gameAccounts.forEach(gameAccountDAO::save);

        final GameAccount first = gameAccounts.getFirst();
        final GameAccountResponse dto = new GameAccountResponse(
            first.getId(),
            first.getName(),
            first.getDiscordAccount().getId()
        );

        return ResponseEntity.ok(dto);
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.RENAME_GAME_ACCOUNTS)
    @PostMapping(value = RENAME, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @GameAccountControllerOperation.Rename
    @AuthorityOperation
    @UnknownAccountOperation
    @NicknameAlreadyTakenOperation
    public ResponseEntity<Void> rename(
        @RequestBody GameAccountRenameRequest request, @AuthData TokenData token
    ) {
        final List<GameAccount> gameAccounts = gameAccountDAO.findByNameIgnoreCase(request.getOldName());
        if (CollectionUtils.isEmpty(gameAccounts)) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        if (gameAccounts.size() > 1) {
            logger.warn("{} accounts by name {}", gameAccounts.size(), request.getOldName());
        }

        if (!CollectionUtils.isEmpty(gameAccountDAO.findByNameIgnoreCase(request.getNewName()))) {
            throw AccountException.NICKNAME_ALREADY_TAKEN;
        }

        gameAccounts.forEach(gameAccount -> gameAccount.setName(request.getNewName()));
        gameAccountDAO.saveAll(gameAccounts);

        return ResponseEntity.ok().build();
    }
}
