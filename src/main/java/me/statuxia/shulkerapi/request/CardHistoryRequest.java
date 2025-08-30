package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.statuxia.shulkerapi.model.BankCardHistoryType;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardHistoryRequest extends CardRequest {

    protected List<BankCardHistoryType> types;

    public List<BankCardHistoryType> getTypes() {
        return types;
    }

    public void setTypes(List<BankCardHistoryType> types) {
        this.types = types;
    }
}
