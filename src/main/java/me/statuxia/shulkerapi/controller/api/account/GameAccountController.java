package me.statuxia.shulkerapi.controller.api.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.controller.api.Controller;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.GameAccountBalanceHistoryDAO;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dao.impl.GameAccountBalanceDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.search.impl.GameAccountBalanceSearchDTO;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.request.*;
import me.statuxia.shulkerapi.response.GameAccountBalanceResponse;
import me.statuxia.shulkerapi.response.GameAccountResponse;
import me.statuxia.shulkerapi.response.NamedItem;
import me.statuxia.shulkerapi.service.DiscordAccountService;
import me.statuxia.shulkerapi.service.impl.MessageService;
import me.statuxia.shulkerapi.swagger.AuthorityOperation;
import me.statuxia.shulkerapi.swagger.IncorrectDataOperation;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.GameAccountControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.account.AccessDeniedOperation;
import me.statuxia.shulkerapi.swagger.controller.account.AlreadyLinkedAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.account.NicknameAlreadyTakenOperation;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = GameAccountController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Tag(name = "Game Profile", description = "Контроллер для создания и получения игровых профилей")
public class GameAccountController implements Controller {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String PREFIX = "/api/v1/game-account";
    public static final String GET = "/get";
    public static final String CREATE = "/create";
    public static final String RENAME = "/rename";
    public static final String BALANCE_GET = "/balance/get";
    public static final String BALANCE_TYPES = "/balance/types";
    public static final String BALANCE_CHANGE = "/balance/change";

    private final GameAccountDAO gameAccountDAO;
    private final GameAccountBalanceDAO gameAccountBalanceDAO;
    private final DiscordAccountService discordAccountService;
    private final GameAccountBalanceHistoryDAO gameAccountBalanceHistoryDAO;
    private final MessageService messageService;

    @Autowired
    public GameAccountController(
        GameAccountDAO gameAccountDAO,
        GameAccountBalanceDAO gameAccountBalanceDAO,
        DiscordAccountService discordAccountService,
        GameAccountBalanceHistoryDAO gameAccountBalanceHistoryDAO,
        MessageService messageService
    ) {
        this.gameAccountDAO = gameAccountDAO;
        this.gameAccountBalanceDAO = gameAccountBalanceDAO;
        this.discordAccountService = discordAccountService;
        this.gameAccountBalanceHistoryDAO = gameAccountBalanceHistoryDAO;
        this.messageService = messageService;
    }

    @GetMapping(value = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @GameAccountControllerOperation.Get
    public ResponseEntity<GameAccountResponse> get(
        @RequestBody GameAccountGetRequest request, @AuthData TokenData tokenData
    ) {
        final List<GameAccountResponse> list = gameAccountDAO.findByNameIgnoreCase(request.getName()).stream()
            .map(account -> new GameAccountResponse(
                account.getId(), account.getName(),
                account.getDiscordAccount() == null ? null : account.getDiscordAccount().getId()
            )).toList();

        if (list.isEmpty()) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        return ResponseEntity.ok(list.getFirst());
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
        final Long discordId = request.getDiscordId();
        if (!StringUtils.hasText(name) || discordId == null) {
            throw BaseApiException.INCORRECT_DATA;
        }

        final DiscordAccount discordAccount = discordAccountService.createOrGet(discordId);
        final List<GameAccount> gameAccountsByDiscord = gameAccountDAO.findByDiscordAccount(discordAccount);
        final List<GameAccount> gameAccounts = gameAccountDAO.findByNameIgnoreCase(name);

        final boolean hasAccounts = !CollectionUtils.isEmpty(gameAccountsByDiscord);
        final boolean twink = request.isTwink() || hasAccounts;

        /**
         * аккаунтов нет, но пытаемся создать твинк
         */
        if (CollectionUtils.isEmpty(gameAccountsByDiscord) && request.isTwink()) {
            logger.debug("[discord #{}] no accounts. return exception", discordId);
            throw AccountException.NO_LINKED_ACCOUNTS;
        }

        /**
         * Аккаунт уже привязан, но к другому discord
         */
        if (!gameAccounts.isEmpty() && !gameAccounts.getFirst().getDiscordAccount().getId().equals(discordId)) {
            logger.debug("[discord #{}] can't create account with name {}. already linked", discordId, name);
            throw AccountException.ALREADY_LINKED_ACCOUNT;
        }

        if (gameAccounts.isEmpty()) {
            final GameAccount account = new GameAccount();
            account.setName(name);
            account.setDiscordAccount(discordAccount);
            gameAccounts.add(account);
            account.setPaid(twink);
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

    @GetMapping(value = BALANCE_GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @AccessDeniedOperation
    @GameAccountControllerOperation.BalanceGet
    public ResponseEntity<GameAccountBalanceResponse> balance(
        @RequestBody GameAccountBalanceRequest request, @AuthData TokenData token
    ) {
        final List<GameAccount> gameAccounts = gameAccountDAO.findByNameIgnoreCase(request.getGameAccount());
        if (CollectionUtils.isEmpty(gameAccounts)) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        GameAccount gameAccount = null;
        for (GameAccount account : gameAccounts) {
            if (!Objects.equals(token.getDiscordAccount().getId(), account.getDiscordAccount().getId())) {
                continue;
            }

            gameAccount = account;
            break;
        }

        if (gameAccount == null) {
            throw AccountException.ACCESS_DENIED;
        }

        final Optional<GameAccountBalance> optBalance = gameAccountBalanceDAO.find(
            new GameAccountBalanceSearchDTO()
                .setGameAccount(gameAccounts.getFirst())
                .setType(request.getType())
        );

        return ResponseEntity.ok(optBalance.map(balance -> {
            final GameAccountBalanceResponse response = new GameAccountBalanceResponse();
            response.setType(balance.getType());
            response.setValue(balance.getValue());
            return response;
        }).orElseGet(() -> {
            final GameAccountBalanceResponse response = new GameAccountBalanceResponse();
            response.setType(request.getType());
            response.setValue(0L);
            return response;
        }));
    }

    @GetMapping(value = BALANCE_TYPES, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @GameAccountControllerOperation.BalanceTypes
    public ResponseEntity<List<NamedItem>> balanceTypes(@AuthData TokenData token) {
        return ResponseEntity.ok(Arrays.stream(GameAccountBalanceType.values())
            .map(type -> new NamedItem(messageService.message(type), type.name()))
            .toList());
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.GAME_ACCOUNT_BALANCE_CHANGE)
    @GetMapping(value = BALANCE_CHANGE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @GameAccountControllerOperation.BalanceChange
    public ResponseEntity<Void> balanceChange(
        @RequestBody GameAccountBalanceChangeRequest request, @AuthData TokenData token
    ) {
        final List<GameAccount> gameAccounts = gameAccountDAO.findByNameIgnoreCase(request.getGameAccount());
        if (CollectionUtils.isEmpty(gameAccounts)) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        GameAccount gameAccount = null;
        for (GameAccount account : gameAccounts) {
            if (!Objects.equals(token.getDiscordAccount().getId(), account.getDiscordAccount().getId())) {
                continue;
            }

            gameAccount = account;
            break;
        }

        if (gameAccount == null) {
            throw AccountException.ACCESS_DENIED;
        }

        final GameAccountBalance balance = gameAccountBalanceDAO.find(
            new GameAccountBalanceSearchDTO()
                .setGameAccount(gameAccounts.getFirst())
                .setType(request.getType())
        ).orElse(new GameAccountBalance());
        final long oldBalance = balance.getValue() == null ? 0L : balance.getValue();

        balance.setGameAccount(gameAccount);
        balance.setType(request.getType());
        balance.setValue(oldBalance);

        gameAccountBalanceDAO.save(balance);

        balance.setValue(oldBalance + request.getValue());
        gameAccountBalanceDAO.save(balance);

        final GameAccountBalanceHistory history = new GameAccountBalanceHistory();
        history.setCreateDate(DateTime.now());
        history.setGameAccount(gameAccount);
        history.setType(request.getType());
        history.setGameAccountBalance(balance);

        final ObjectMapper mapper = new ObjectMapper();
        try {
            final String jsonData = mapper.writeValueAsString(Map.of(
                "oldBalance", oldBalance, "newBalance", balance.getValue()
            ));
            final JsonNode jsonNode = mapper.readTree(jsonData);
            history.setData(jsonNode);
        } catch (JsonProcessingException e) {
            logger.error("caught error on jsonData");
        }

        gameAccountBalanceHistoryDAO.save(history);

        return ResponseEntity.ok().build();
    }
}
