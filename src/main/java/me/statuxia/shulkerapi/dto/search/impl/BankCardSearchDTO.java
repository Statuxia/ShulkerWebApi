package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.CardType;
import me.statuxia.shulkerapi.model.GameAccount;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;

public class BankCardSearchDTO implements ISearchDTO {

    protected String number;
    protected GameAccount gameAccount;
    protected CardType cardType;

    protected DateTime startCreateTime;
    protected DateTime endCreateTime;

    protected Pageable pageable;

    public String getNumber() {
        return number;
    }

    public BankCardSearchDTO setNumber(String number) {
        this.number = number;
        return this;
    }

    public GameAccount getGameAccount() {
        return gameAccount;
    }

    public BankCardSearchDTO setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }

    public CardType getCardType() {
        return cardType;
    }

    public BankCardSearchDTO setCardType(CardType cardType) {
        this.cardType = cardType;
        return this;
    }

    public DateTime getStartCreateTime() {
        return startCreateTime;
    }

    public BankCardSearchDTO setStartCreateTime(DateTime startCreateTime) {
        this.startCreateTime = startCreateTime;
        return this;
    }

    public DateTime getEndCreateTime() {
        return endCreateTime;
    }

    public BankCardSearchDTO setEndCreateTime(DateTime endCreateTime) {
        this.endCreateTime = endCreateTime;
        return this;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public BankCardSearchDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }
}
