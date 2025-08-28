package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import org.springframework.stereotype.Service;

@Service
public interface GameAccountService {

    /**
     * Получение игрового аккаунта по имени с проверкой авторити или пренадлежности токена к аккаунту
     */
    GameAccount getGameAccount(
        TokenData token,
        String name,
        TokenAuthorityEnum authorityForSkip
    );

    /**
     * Получение игрового аккаунта по имени с выбрасыванием исключения при отсутствии
     */
    GameAccount getGameAccount(String name);


    /**
     * Валидация пренадлежность аккаунта
     *
     * @throws me.statuxia.shulkerapi.exception.AccountException, если не пренадлежит
     */
    void validateOwned(
        TokenData token,
        GameAccount account
    );

    /**
     * Валидация пренадлежность аккаунта
     *
     * @return true, если пренадлежит
     */
    boolean validateOwnedWithResult(
        TokenData token,
        GameAccount account
    );
}
