package me.statuxia.shulkerapi.controller.api.account;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.controller.api.AuthController;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dao.PaidAccountDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.PaidAccount;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.GameAccountCreateRequest;
import me.statuxia.shulkerapi.response.CanCreateTwinkResponse;
import me.statuxia.shulkerapi.response.GameAccountResponse;
import me.statuxia.shulkerapi.service.DiscordAccountService;
import me.statuxia.shulkerapi.swagger.AuthorityOperation;
import me.statuxia.shulkerapi.swagger.IncorrectDataOperation;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.GameAccountControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.account.AlreadyLinkedAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.account.NotPaidAccountOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = GameAccountController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Tag(name = "Game Profile", description = "Контроллер для создания и получения игровых профилей")
public class GameAccountController implements AuthController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String PREFIX = "/api/v1/game-account";
    public static final String CREATE = "/create";
    public static final String CAN_CREATE_TWINK = "/can-create-twink";

    private final PaidAccountDAO paidAccountDAO;
    private final GameAccountDAO gameAccountDAO;
    private final DiscordAccountService discordAccountService;

    @Autowired
    public GameAccountController(
        PaidAccountDAO paidAccountDAO,
        GameAccountDAO gameAccountDAO,
        DiscordAccountService discordAccountService
    ) {
        this.paidAccountDAO = paidAccountDAO;
        this.gameAccountDAO = gameAccountDAO;
        this.discordAccountService = discordAccountService;
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.ADD_GAME_ACCOUNTS)
    @PostMapping(value = CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @GameAccountControllerOperation.Create
    @AuthorityOperation
    @IncorrectDataOperation
    @AlreadyLinkedAccountOperation
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

        if (!gameAccounts.isEmpty() && request.isTwink()) {
            throw AccountException.ALREADY_LINKED_ACCOUNT;
        }

        if (gameAccounts.isEmpty()) {
            final DiscordAccount discordAccount = discordAccountService.createOrGet(request.getDiscordId());

            final GameAccount account = new GameAccount();
            account.setName(name);
            account.setDiscordAccount(discordAccount);
            gameAccounts.add(account);

            final List<PaidAccount> paidAccounts = paidAccountDAO.findByNameIgnoreCase(name);
            if (!CollectionUtils.isEmpty(paidAccounts)) {
                paidAccountDAO.deleteById(paidAccounts.getFirst().getId());
                account.setPaid(true);
            }
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

    @RequiredAuthority(requireAll = TokenAuthorityEnum.CAN_CREATE_TWINK)
    @GetMapping(value = CAN_CREATE_TWINK, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @NotPaidAccountOperation
    public ResponseEntity<CanCreateTwinkResponse> canCreateTwink(
        @RequestParam("paidAccount") String name, @AuthData TokenData token
    ) {
        if (CollectionUtils.isEmpty(paidAccountDAO.findByNameIgnoreCase(name))) {
            throw AccountException.NOT_PAID_ACCOUNT;
        }

        final List<GameAccount> gameAccounts = gameAccountDAO.findByNameIgnoreCase(name);
        if (CollectionUtils.isEmpty(gameAccounts)) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        return ResponseEntity.ok(
            new CanCreateTwinkResponse()
                .setCanCreate(true)
                .setDiscordId(gameAccounts.getFirst().getDiscordAccount().getId())
        );
    }
}
