package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GameAccountServiceImpl implements GameAccountService {

    private final TokenService tokenService;
    private final GameAccountDAO gameAccountDAO;
    private final GameAccountService gameAccountService;

    @Autowired
    public GameAccountServiceImpl(
        TokenService tokenService,
        GameAccountDAO gameAccountDAO,
        @Lazy GameAccountService gameAccountService
    ) {
        this.tokenService = tokenService;
        this.gameAccountDAO = gameAccountDAO;
        this.gameAccountService = gameAccountService;
    }

    /**
     * Получение игрового аккаунта по имени с проверкой авторити или пренадлежности токена к аккаунту
     */
    @Transactional
    public GameAccount getGameAccount(
        TokenData token, String name,
        TokenAuthorityEnum authorityForSkip
    ) {
        final GameAccount account = gameAccountService.getGameAccount(name);

        if (!tokenService.hasAuthority(token.token(), authorityForSkip)) {
            gameAccountService.validateOwned(token, account);
        }

        return account;
    }

    /**
     * Получение игрового аккаунта по имени с выбрасыванием исключения при отсутствии
     */
    @Transactional
    public GameAccount getGameAccount(String name) {
        final Optional<GameAccount> optAccount = gameAccountDAO.findByName(name);
        if (optAccount.isEmpty()) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }
        return optAccount.get();
    }

    /**
     * Валидация пренадлежность аккаунта
     *
     * @throws me.statuxia.shulkerapi.exception.AccountException, если не пренадлежит
     */
    @Transactional
    @Override
    public void validateOwned(
        TokenData token,
        GameAccount account
    ) {
        if (!gameAccountService.validateOwnedWithResult(token, account)) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }
    }

    /**
     * Валидация пренадлежность аккаунта
     *
     * @return true, если пренадлежит
     */
    @Transactional
    @Override
    public boolean validateOwnedWithResult(
        TokenData token,
        GameAccount account
    ) {
        final List<GameAccount> currentAccounts = gameAccountDAO.findByDiscordAccount(token.getDiscordAccount());
        return currentAccounts.contains(account);
    }
}
