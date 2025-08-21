package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.CustomToken;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.CardCreateRequest;
import me.statuxia.shulkerapi.service.AccountService;
import me.statuxia.shulkerapi.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final TokenService tokenService;
    private final GameAccountDAO gameAccountDAO;

    @Autowired
    public AccountServiceImpl(TokenService tokenService, GameAccountDAO gameAccountDAO) {
        this.tokenService = tokenService;
        this.gameAccountDAO = gameAccountDAO;
    }

    /**
     * Получение аккаунта с проверкой пренадлежности к токену
     */
    @Transactional
    public GameAccount getOwner(
        TokenData token,
        CardCreateRequest request,
        TokenAuthorityEnum authorityForSkip
    ) {
        final GameAccount account = getOwner(request);

        if (!tokenService.hasAuthority(token.token(), authorityForSkip)) {
            validateOwned(token, account);
        }

        return account;
    }

    /**
     * Валидация пренадлежность аккаунта
     */
    @Transactional
    public void validateOwned(
        TokenData token,
        GameAccount account
    ) {
        final List<GameAccount> currentAccounts = getCurrentAccounts(token);
        if (!currentAccounts.contains(account)) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }
    }

    /**
     * Получение аккаунта не проверяя пренадлежность к токену
     */
    private GameAccount getOwner(CardCreateRequest request) {
        final Optional<GameAccount> optAccount = gameAccountDAO.findByName(request.getName());
        if (optAccount.isEmpty()) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }
        return optAccount.get();
    }

    /**
     * Получение аккаунтов по токену
     */
    private List<GameAccount> getCurrentAccounts(TokenData token) {
        return switch (token.source()) {
            case DiscordAccount discordAccount -> gameAccountDAO.findByDiscordAccount(discordAccount);
            case CustomToken customToken -> gameAccountDAO.findByDiscordAccount(
                customToken.getAccount().getDiscordAccount()
            );
            default -> throw BaseApiException.INCORRECT_DATA;
        };
    }
}
