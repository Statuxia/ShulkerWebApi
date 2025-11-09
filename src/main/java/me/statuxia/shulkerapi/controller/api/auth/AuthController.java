package me.statuxia.shulkerapi.controller.api.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.configuration.properties.AuthProperties;
import me.statuxia.shulkerapi.controller.api.Controller;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dao.impl.GameSessionIpDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.search.impl.GameSessionIpDTO;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.exception.GameSessionIpException;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.GameSessionIp;
import me.statuxia.shulkerapi.model.GameSessionIpState;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.AuthChangeStateRequest;
import me.statuxia.shulkerapi.request.AuthRefreshRequest;
import me.statuxia.shulkerapi.request.AuthValidateRequest;
import me.statuxia.shulkerapi.request.PaginationRequest;
import me.statuxia.shulkerapi.response.AuthValidateResponse;
import me.statuxia.shulkerapi.response.GameSessionIpItem;
import me.statuxia.shulkerapi.response.GameSessionIpResponse;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.auth.UnknownGameSessionOperation;
import me.statuxia.shulkerapi.swagger.controller.auth.UnsupportedRequestStateOperation;
import me.statuxia.shulkerapi.swagger.controller.auth.UnsupportedToChangeStateOperation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static me.statuxia.shulkerapi.model.GameSessionIpState.*;

@RestController
@RequestMapping(value = AuthController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Tag(name = "Auth", description = "Авторизационные эндпоинты")
public class AuthController implements Controller {

    public static final String PREFIX = "/api/v1/auth";
    public static final String VALIDATE = "/validate";
    public static final String QUEUE = "/queue";
    public static final String CHANGE_STATE = "/change-state";
    public static final String REFRESH = "/refresh";
    public static final Set<GameSessionIpState> CHANGE_STATE_REQUEST_AVAILABLE_TYPES = Set.of(
        NOT_NOTIFIED,
        ACCEPTED,
        REJECTED
    );
    public static final Set<GameSessionIpState> AVAILABLE_TO_CHANGE_STATES = Set.of(
        STARTED,
        ACCEPTED
    );

    private final GameAccountDAO gameAccountDAO;
    private final GameSessionIpDAO gameSessionIpDAO;
    private final AuthProperties authProperties;

    private final Map<GameSessionIpState, Long> stateLifetimes = new EnumMap<>(GameSessionIpState.class);

    @PostConstruct
    public void init() {
        stateLifetimes.put(STARTED, authProperties.getStartedLifetimeSeconds());
        stateLifetimes.put(ACCEPTED, authProperties.getAcceptedLifetimeSeconds());
        stateLifetimes.put(REJECTED, authProperties.getRejectedLifetimeSeconds());
        stateLifetimes.put(NOT_NOTIFIED, authProperties.getNotNotifiedLifetimeSeconds());
        stateLifetimes.put(OUTDATED, authProperties.getOutdatedLifetimeSeconds());
    }

    @Autowired
    public AuthController(
        GameAccountDAO gameAccountDAO,
        GameSessionIpDAO gameSessionIpDAO,
        AuthProperties authProperties
    ) {
        this.gameAccountDAO = gameAccountDAO;
        this.gameSessionIpDAO = gameSessionIpDAO;
        this.authProperties = authProperties;
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.AUTH_VALIDATE)
    @PostMapping(value = VALIDATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @UnknownAccountOperation
    @Transactional
    public ResponseEntity<AuthValidateResponse> validate(
        @RequestBody @Valid AuthValidateRequest request,
        @AuthData TokenData token
    ) {
        final Optional<GameAccount> gameAccount
            = gameAccountDAO.findByNameIgnoreCase(request.getName()).stream().findFirst();

        if (gameAccount.isEmpty()) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        final GameSessionIpDTO searchDTO = new GameSessionIpDTO()
            .setGameAccount(gameAccount.get())
            .setIp(request.getIp())
            .setLastJoinDateFrom(DateTime.now())
            .setPageable(PageRequest.of(0, 1, Sort.Direction.DESC, "id"));

        final Optional<GameSessionIp> optSessionIp = gameSessionIpDAO.find(searchDTO);

        if (optSessionIp.isEmpty()) {
            final GameSessionIp gameSessionIp = createGameSession(request.getIp(), gameAccount.get());
            gameSessionIpDAO.save(gameSessionIp);

            return ResponseEntity.ok(new AuthValidateResponse().setState(gameSessionIp.getState()));
        }

        final GameSessionIp gameSessionIp = optSessionIp.get();

        return ResponseEntity.ok(new AuthValidateResponse().setState(
            switch (gameSessionIp.getState()) {
                case OUTDATED -> {
                    final GameSessionIp newGameSessionIp = createGameSession(request.getIp(), gameAccount.get());
                    gameSessionIpDAO.save(newGameSessionIp);

                    yield newGameSessionIp.getState();
                }
                case NOT_NOTIFIED -> {
                    final GameSessionIp newGameSessionIp = createGameSession(request.getIp(), gameAccount.get());
                    gameSessionIpDAO.save(newGameSessionIp);

                    yield NOT_NOTIFIED;
                }
                default -> gameSessionIp.getState();
            }
        ));
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.AUTH_QUEUE)
    @PostMapping(value = QUEUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<GameSessionIpResponse> queue(
        @RequestBody PaginationRequest request,
        @AuthData TokenData token
    ) {
        final List<GameSessionIpItem> items = gameSessionIpDAO.findList(
            new GameSessionIpDTO()
                .setPageable(request.getPageable())
                .setLastJoinDateFrom(DateTime.now())
                .setNotified(false)
        ).stream().map(item -> new GameSessionIpItem()
            .setId(item.getId())
            .setName(item.getGameAccount().getName())
            .setDiscordId(item.getGameAccount().getDiscordAccount().getId())
            .setState(item.getState())
            .setIp(item.getIp())).toList();

        final GameSessionIpResponse response = new GameSessionIpResponse();
        response.setTotal(items.size());
        response.setItems(items);
        return ResponseEntity.ok(response);
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.AUTH_CHANGE_STATE)
    @PostMapping(value = CHANGE_STATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownGameSessionOperation
    @UnsupportedRequestStateOperation
    @UnsupportedToChangeStateOperation
    public ResponseEntity<Void> changeState(
        @RequestBody @Valid AuthChangeStateRequest request,
        @AuthData TokenData token
    ) {
        final GameSessionIp session = gameSessionIpDAO.findById(request.getId())
            .orElseThrow(() -> GameSessionIpException.UNKNOWN_GAME_SESSION);

        if (request.getState() == null) {
            session.setNotified(true);
            gameSessionIpDAO.save(session);
            return ResponseEntity.ok().build();
        }

        if (!CHANGE_STATE_REQUEST_AVAILABLE_TYPES.contains(request.getState())) {
            throw GameSessionIpException.UNSUPPORTED_REQUEST_STATE;
        }

        if (!AVAILABLE_TO_CHANGE_STATES.contains(session.getState())) {
            throw GameSessionIpException.UNSUPPORTED_TO_CHANGE_STATE;
        }

        session.setNotified(true);
        session.setState(request.getState());
        session.setLastJoinDate(
            DateTime.now().plusSeconds(stateLifetimes.getOrDefault(request.getState(), 0L).intValue())
        );

        gameSessionIpDAO.save(session);
        return ResponseEntity.ok().build();
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.AUTH_CHANGE_STATE)
    @PostMapping(value = REFRESH, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownGameSessionOperation
    public ResponseEntity<Void> refresh(
        @RequestBody @Valid AuthRefreshRequest request
    ) {
        final Optional<GameAccount> gameAccount
            = gameAccountDAO.findByNameIgnoreCase(request.getName()).stream().findFirst();

        if (gameAccount.isEmpty()) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        final GameSessionIpDTO searchDTO = new GameSessionIpDTO()
            .setGameAccount(gameAccount.get())
            .setIp(request.getIp())
            .setLastJoinDateFrom(DateTime.now())
            .setStates(List.of(ACCEPTED))
            .setPageable(PageRequest.of(0, 1, Sort.Direction.DESC, "id"));

        final Optional<GameSessionIp> optSessionIp = gameSessionIpDAO.find(searchDTO);

        if (optSessionIp.isEmpty()) {
            throw GameSessionIpException.UNKNOWN_GAME_SESSION;
        }

        final GameSessionIp gameSessionIp = optSessionIp.get();
        gameSessionIp.setLastJoinDate(
            DateTime.now().plusSeconds(stateLifetimes.getOrDefault(ACCEPTED, 1L).intValue())
        );
        gameSessionIpDAO.save(gameSessionIp);

        return ResponseEntity.ok().build();
    }

    private GameSessionIp createGameSession(String ip, GameAccount gameAccount) {
        final GameSessionIp gameSessionIp = new GameSessionIp();
        gameSessionIp.setGameAccount(gameAccount);
        gameSessionIp.setIp(ip);
        gameSessionIp.setLastJoinDate(DateTime.now().plusSeconds(
            stateLifetimes.getOrDefault(STARTED, 0L).intValue()
        ));
        gameSessionIp.setState(STARTED);
        return gameSessionIp;
    }

}
