package me.statuxia.shulkerapi.dto;

import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ChangeCurrencyDTO {

    private final BankCard card;
    private final BankCardHistoryType type;
    private final FromToDiff fromToDiff;

    private String message;
    private List<CardHistoryAdditionalData> additionalData = new ArrayList<>();

    public ChangeCurrencyDTO(BankCard card, BankCardHistoryType type, long from, long to, long diff) {
        this.card = card;
        this.type = type;
        this.fromToDiff = new FromToDiff(from, to, diff);
    }

    public ChangeCurrencyDTO(BankCard card, BankCardHistoryType type, FromToDiff fromToDiff) {
        this.card = card;
        this.type = type;
        this.fromToDiff = fromToDiff;
    }

    public BankCard getCard() {
        return card;
    }

    public BankCardHistoryType getType() {
        return type;
    }

    public FromToDiff getFromToDiff() {
        return fromToDiff;
    }

    public String getMessage() {
        return message;
    }

    public ChangeCurrencyDTO setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<CardHistoryAdditionalData> getAdditionalData() {
        return additionalData;
    }

    public ChangeCurrencyDTO setAdditionalData(
        List<CardHistoryAdditionalData> additionalData
    ) {
        this.additionalData.clear();
        if (CollectionUtils.isEmpty(additionalData)) {
            return this;
        }

        this.additionalData.addAll(additionalData);
        return this;
    }

    public ChangeCurrencyDTO addAdditionalData(CardHistoryAdditionalData data) {
        this.additionalData.add(data);
        return this;
    }
}
