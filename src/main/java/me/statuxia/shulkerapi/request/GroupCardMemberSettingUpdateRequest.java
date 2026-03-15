package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import me.statuxia.shulkerapi.model.BankCardSettingType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCardMemberSettingUpdateRequest extends BaseCardRequest {

    private String pin;

    @NotNull
    @NotEmpty
    private String memberGameAccount;

    @NotNull
    private BankCardSettingType type;

    @NotNull
    @NotEmpty
    private String value;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getMemberGameAccount() {
        return memberGameAccount;
    }

    public void setMemberGameAccount(String memberGameAccount) {
        this.memberGameAccount = memberGameAccount;
    }

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
