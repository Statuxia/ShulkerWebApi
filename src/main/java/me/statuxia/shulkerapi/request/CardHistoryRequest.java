package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Pattern;
import me.statuxia.shulkerapi.model.BankCardHistoryType;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardHistoryRequest extends CardListRequest {

    protected List<@Pattern(regexp = "\\d{4} \\d{4}") String> cardNumbers;
    protected List<BankCardHistoryType> historyTypes;

    public List<BankCardHistoryType> getHistoryTypes() {
        return historyTypes;
    }

    public void setHistoryTypes(List<BankCardHistoryType> historyTypes) {
        this.historyTypes = historyTypes;
    }

    public List<String> getCardNumbers() {
        return cardNumbers;
    }

    public void setCardNumbers(List<String> cardNumbers) {
        this.cardNumbers = cardNumbers;
    }
}
