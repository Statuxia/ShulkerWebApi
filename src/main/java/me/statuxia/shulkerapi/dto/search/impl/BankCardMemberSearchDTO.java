package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.GameAccount;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;

public class BankCardMemberSearchDTO implements ISearchDTO {

    protected BankCard card;
    protected GameAccount gameAccount;
    protected DateTime addedAtFrom;
    protected DateTime addedAtTo;
    protected Long creditedFrom;
    protected Long creditedTo;
    protected Long debitedFrom;
    protected Long debitedTo;
    protected Pageable pageable;

    public BankCard getCard() {
        return card;
    }

    public BankCardMemberSearchDTO setCard(BankCard card) {
        this.card = card;
        return this;
    }

    public GameAccount getGameAccount() {
        return gameAccount;
    }

    public BankCardMemberSearchDTO setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }

    public DateTime getAddedAtFrom() {
        return addedAtFrom;
    }

    public BankCardMemberSearchDTO setAddedAtFrom(DateTime addedAtFrom) {
        this.addedAtFrom = addedAtFrom;
        return this;
    }

    public DateTime getAddedAtTo() {
        return addedAtTo;
    }

    public BankCardMemberSearchDTO setAddedAtTo(DateTime addedAtTo) {
        this.addedAtTo = addedAtTo;
        return this;
    }

    public Long getCreditedFrom() {
        return creditedFrom;
    }

    public BankCardMemberSearchDTO setCreditedFrom(Long creditedFrom) {
        this.creditedFrom = creditedFrom;
        return this;
    }

    public Long getCreditedTo() {
        return creditedTo;
    }

    public BankCardMemberSearchDTO setCreditedTo(Long creditedTo) {
        this.creditedTo = creditedTo;
        return this;
    }

    public Long getDebitedFrom() {
        return debitedFrom;
    }

    public BankCardMemberSearchDTO setDebitedFrom(Long debitedFrom) {
        this.debitedFrom = debitedFrom;
        return this;
    }

    public Long getDebitedTo() {
        return debitedTo;
    }

    public BankCardMemberSearchDTO setDebitedTo(Long debitedTo) {
        this.debitedTo = debitedTo;
        return this;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public BankCardMemberSearchDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }
}
