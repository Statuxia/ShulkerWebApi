package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardMember;
import me.statuxia.shulkerapi.model.BankCardSettingType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class BankCardMemberSettingSearchDTO implements ISearchDTO {

    protected BankCard card;
    protected BankCardMember bankCardMember;
    protected BankCardSettingType type;
    protected List<BankCardSettingType> types;
    protected Pageable pageable;

    public BankCard getCard() {
        return card;
    }

    public BankCardMemberSettingSearchDTO setCard(BankCard card) {
        this.card = card;
        return this;
    }

    public BankCardMember getBankCardMember() {
        return bankCardMember;
    }

    public BankCardMemberSettingSearchDTO setBankCardMember(BankCardMember bankCardMember) {
        this.bankCardMember = bankCardMember;
        return this;
    }

    public BankCardSettingType getType() {
        return type;
    }

    public BankCardMemberSettingSearchDTO setType(BankCardSettingType type) {
        this.type = type;
        return this;
    }

    public List<BankCardSettingType> getTypes() {
        return types;
    }

    public BankCardMemberSettingSearchDTO setTypes(List<BankCardSettingType> types) {
        this.types = types;
        return this;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public BankCardMemberSettingSearchDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }
}
