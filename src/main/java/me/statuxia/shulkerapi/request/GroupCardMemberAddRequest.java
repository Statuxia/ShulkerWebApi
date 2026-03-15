package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCardMemberAddRequest extends BaseCardRequest {

    @NotNull
    @NotEmpty
    private String memberGameAccount;

    @Pattern(regexp = "\\d{4}")
    @NotNull
    private String pin;

    public String getMemberGameAccount() {
        return memberGameAccount;
    }

    public void setMemberGameAccount(String memberGameAccount) {
        this.memberGameAccount = memberGameAccount;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
