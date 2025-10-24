package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public class BankCardHistorySearchDTO implements ISearchDTO {

    protected List<Long> ids;
    protected UUID uuid;
    protected BankCard card;
    protected List<BankCard> cards;
    protected List<BankCardHistoryType> types;

    protected DateTime startCreateTime;
    protected DateTime endCreateTime;

    protected Pageable pageable;

    public List<Long> getIds() {
        return ids;
    }

    public BankCardHistorySearchDTO setIds(List<Long> ids) {
        this.ids = ids;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    public BankCardHistorySearchDTO setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public BankCard getCard() {
        return card;
    }

    public BankCardHistorySearchDTO setCard(BankCard card) {
        this.card = card;
        return this;
    }

    public List<BankCard> getCards() {
        return cards;
    }

    public BankCardHistorySearchDTO setCards(List<BankCard> cards) {
        this.cards = cards;
        return this;
    }

    public List<BankCardHistoryType> getTypes() {
        return types;
    }

    public BankCardHistorySearchDTO setTypes(List<BankCardHistoryType> types) {
        this.types = types;
        return this;
    }

    public DateTime getStartCreateTime() {
        return startCreateTime;
    }

    public BankCardHistorySearchDTO setStartCreateTime(DateTime startCreateTime) {
        this.startCreateTime = startCreateTime;
        return this;
    }

    public DateTime getEndCreateTime() {
        return endCreateTime;
    }

    public BankCardHistorySearchDTO setEndCreateTime(DateTime endCreateTime) {
        this.endCreateTime = endCreateTime;
        return this;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public BankCardHistorySearchDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }
}
