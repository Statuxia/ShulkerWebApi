package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCardMemberUpdatePinRequest extends BaseCardRequest {

    private String pin;

    @NotNull
    @NotEmpty
    private String memberGameAccount;

    @Pattern(regexp = "\\d{4}")
    @NotNull
    private String newPin;

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

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }
}
