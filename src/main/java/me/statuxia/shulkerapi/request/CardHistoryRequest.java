package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.statuxia.shulkerapi.model.BankCardHistoryType;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardHistoryRequest extends CardRequest {

    protected List<BankCardHistoryType> historyTypes;

    public List<BankCardHistoryType> getHistoryTypes() {
        return historyTypes;
    }

    public void setHistoryTypes(List<BankCardHistoryType> historyTypes) {
        this.historyTypes = historyTypes;
    }
}
