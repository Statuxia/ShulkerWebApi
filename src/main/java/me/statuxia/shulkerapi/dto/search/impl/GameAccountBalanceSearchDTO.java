package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.GameAccountBalanceType;
import org.springframework.data.domain.Pageable;

public class GameAccountBalanceSearchDTO implements ISearchDTO {

    protected GameAccount gameAccount;
    protected GameAccountBalanceType type;

    protected Pageable pageable;

    public GameAccount getGameAccount() {
        return gameAccount;
    }

    public GameAccountBalanceSearchDTO setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }

    public GameAccountBalanceType getType() {
        return type;
    }

    public GameAccountBalanceSearchDTO setType(GameAccountBalanceType type) {
        this.type = type;
        return this;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public GameAccountBalanceSearchDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }
}
