package me.statuxia.shulkerapi.controller.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.configuration.properties.SessionLimitationProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.AccountDAO;
import me.statuxia.shulkerapi.dao.CustomTokenDAO;
import me.statuxia.shulkerapi.dao.TokenAuthorityDAO;
import me.statuxia.shulkerapi.dao.TokenLimitationDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.request.TokenCreateRequest;
import me.statuxia.shulkerapi.response.ApiExceptionResponse;
import me.statuxia.shulkerapi.response.TokenLimitationResponse;
import me.statuxia.shulkerapi.response.TokenResponse;
import me.statuxia.shulkerapi.service.CodeTokenService;
import me.statuxia.shulkerapi.swagger.AuthOperation;
import me.statuxia.shulkerapi.swagger.AuthorityOperation;
import me.statuxia.shulkerapi.swagger.IncorrectDataOperation;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.TokenControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.account.AlreadyLinkedAccountOperation;
import me.statuxia.shulkerapi.utils.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static me.statuxia.shulkerapi.exception.AuthenticationException.UNKNOWN_TOKEN;
import static me.statuxia.shulkerapi.utils.TokenGenerator.CUSTOM_TOKEN_LENGTH;

@RestController
@RequestMapping(value = TokenController.PREFIX)
@Tag(name = "Token", description = "Контроллер для работы с токеном")
public class TokenController {

    public static final String PREFIX = "/api/v1/token";
    public static final String INFO = "/info";
    public static final String CREATE = "/create";
    public static final String GET_BY_CODE = "/get-by-code";

    private final SessionLimitationProperties sessionLimitationProperties;
    private final CodeTokenService codeTokenService;
    private final CustomTokenDAO customTokenDAO;
    private final TokenLimitationDAO tokenLimitationDAO;
    private final TokenAuthorityDAO tokenAuthorityDAO;
    private final AccountDAO accountDAO;

    @Autowired
    public TokenController(
        SessionLimitationProperties sessionLimitationProperties,
        CodeTokenService codeTokenService,
        TokenLimitationDAO tokenLimitationDAO,
        TokenAuthorityDAO tokenAuthorityDAO,
        AccountDAO accountDAO,
        CustomTokenDAO customTokenDAO
    ) {
        this.sessionLimitationProperties = sessionLimitationProperties;
        this.codeTokenService = codeTokenService;
        this.tokenLimitationDAO = tokenLimitationDAO;
        this.tokenAuthorityDAO = tokenAuthorityDAO;
        this.accountDAO = accountDAO;
        this.customTokenDAO = customTokenDAO;
    }

    @GetMapping(
        value = INFO,
        produces = MediaType.APPLICATION_JSON_VALUE,
        headers = AuthDataResolver.X_TOKEN_HEADER
    )
    @Transactional
    @AuthOperation
    @TokenControllerOperation.Info
    public ResponseEntity<TokenResponse> info(@AuthData TokenData token) {
        final String stringToken = token.token();

        final Account account = token.getAccount();
        final Optional<TokenLimitationResponse> limitationResponse = tokenLimitationDAO.findById(stringToken)
            .map(limitation -> new TokenLimitationResponse()
                .setRateLimit(limitation.getRateLimit())
                .setRateResetSeconds(limitation.getRateResetSeconds()));
        final List<TokenAuthorityEnum> authorities = tokenAuthorityDAO.findByToken(stringToken).stream()
            .map(TokenAuthority::getAuthority).toList();

        return ResponseEntity.ok(
            new TokenResponse()
                .setToken(stringToken)
                .setAccountId(account.getId())
                .setLimitation(limitationResponse.orElse(null))
                .setAuthorities(authorities)
        );
    }

    @GetMapping(value = GET_BY_CODE + "/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ApiResponse(
        responseCode = "1101", description = "Неизвестный токен",
        content = @Content(schema = @Schema(implementation = ApiExceptionResponse.class))
    )
    @TokenControllerOperation.Get
    @AlreadyLinkedAccountOperation
    public ResponseEntity<TokenResponse> getByCode(
        @PathVariable("code") String code
    ) {
        final SessionToken token = codeTokenService.getByCode(code).orElseThrow(() -> UNKNOWN_TOKEN);

        final Account account = token.getAccount();

        final String stringToken = token.getToken();
        final Optional<TokenLimitationResponse> limitationResponse = tokenLimitationDAO.findById(stringToken)
            .map(limitation -> new TokenLimitationResponse()
                .setRateLimit(limitation.getRateLimit())
                .setRateResetSeconds(limitation.getRateResetSeconds()));
        final List<TokenAuthorityEnum> authorities = tokenAuthorityDAO.findByToken(stringToken).stream()
            .map(TokenAuthority::getAuthority).toList();

        return ResponseEntity.ok(
            new TokenResponse()
                .setToken(stringToken)
                .setAccountId(account.getId())
                .setLimitation(limitationResponse.orElse(null))
                .setAuthorities(authorities)
        );
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.CREATE_CUSTOM_TOKENS)
    @PostMapping(
        value = CREATE,
        produces = MediaType.APPLICATION_JSON_VALUE,
        headers = AuthDataResolver.X_TOKEN_HEADER
    )
    @Transactional
    @AuthOperation
    @TokenControllerOperation.Create
    @AuthorityOperation
    @IncorrectDataOperation
    @UnknownAccountOperation
    public ResponseEntity<TokenResponse> create(
        @RequestBody TokenCreateRequest request, @AuthData TokenData token
    ) {
        if (request.getAccountId() == null) {
            throw BaseApiException.INCORRECT_DATA;
        }

        final Optional<Account> account = accountDAO.findById(request.getAccountId());
        if (account.isEmpty()) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        final CustomToken customToken = buildToken(account.get());
        final TokenLimitation limitation = buildLimitation(request, customToken);
        final List<TokenAuthority> authorities = buildAuthorities(request, customToken);

        customTokenDAO.save(customToken);
        tokenLimitationDAO.save(limitation);
        authorities.forEach(tokenAuthorityDAO::save);

        final TokenLimitationResponse limitationResponse = new TokenLimitationResponse()
            .setRateLimit(limitation.getRateLimit())
            .setRateResetSeconds(limitation.getRateResetSeconds());

        return ResponseEntity.ok(
            new TokenResponse()
                .setToken(customToken.getToken())
                .setAccountId(account.orElse(new Account()).getId())
                .setLimitation(limitationResponse)
                .setAuthorities(request.getAuthorities())
        );
    }

    private CustomToken buildToken(Account account) {
        final CustomToken customToken = new CustomToken();
        customToken.setToken(TokenGenerator.generate(CUSTOM_TOKEN_LENGTH));
        customToken.setAccount(account);
        return customToken;
    }

    private List<TokenAuthority> buildAuthorities(
        TokenCreateRequest request, CustomToken customToken) {
        return request.getAuthorities().stream().map(authority -> {
            final TokenAuthority tokenAuthority = new TokenAuthority();
            tokenAuthority.setId(customToken.getToken());
            tokenAuthority.setAuthority(authority);
            return tokenAuthority;
        }).toList();
    }

    private TokenLimitation buildLimitation(TokenCreateRequest request, CustomToken customToken) {
        final TokenLimitation limitation = new TokenLimitation();
        limitation.setId(customToken.getToken());
        limitation.setRateLimit(
            request.getRateLimit() == null
                ? sessionLimitationProperties.getSessionRateLimit()
                : request.getRateLimit()
        );
        limitation.setRateResetSeconds(
            request.getRateResetSeconds() == null
                ? sessionLimitationProperties.getSessionRateResetSeconds()
                : request.getRateResetSeconds()
        );
        return limitation;
    }
}
