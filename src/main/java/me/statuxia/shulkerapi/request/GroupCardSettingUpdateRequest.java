package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import me.statuxia.shulkerapi.model.BankCardSettingType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCardSettingUpdateRequest extends BaseCardRequest {

    @NotNull
    private BankCardSettingType type;

    @NotNull
    @NotEmpty
    private String value;

    public BankCardSettingType getType() {
        return type;
    }

    public void setType(BankCardSettingType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
