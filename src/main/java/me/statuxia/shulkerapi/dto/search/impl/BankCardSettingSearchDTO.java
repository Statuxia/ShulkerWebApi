package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardSettingType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class BankCardSettingSearchDTO implements ISearchDTO {

    protected BankCard card;
    protected BankCardSettingType type;
    protected List<BankCardSettingType> types;
    protected Pageable pageable;

    public BankCard getCard() {
        return card;
    }

    public BankCardSettingSearchDTO setCard(BankCard card) {
        this.card = card;
        return this;
    }

    public BankCardSettingType getType() {
        return type;
    }

    public BankCardSettingSearchDTO setType(BankCardSettingType type) {
        this.type = type;
        return this;
    }

    public List<BankCardSettingType> getTypes() {
        return types;
    }

    public BankCardSettingSearchDTO setTypes(List<BankCardSettingType> types) {
        this.types = types;
        return this;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public BankCardSettingSearchDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }
}
