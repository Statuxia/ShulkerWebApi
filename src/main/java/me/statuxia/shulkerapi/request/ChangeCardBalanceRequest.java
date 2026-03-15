package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeCardBalanceRequest extends BaseCardRequest {

    @Pattern(regexp = "\\d{4}")
    private String pin;

    @Min(1)
    private Long funds;

    private String memberGameAccount;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Long getFunds() {
        return funds;
    }

    public void setFunds(Long funds) {
        this.funds = funds;
    }

    public String getMemberGameAccount() {
        return memberGameAccount;
    }

    public void setMemberGameAccount(String memberGameAccount) {
        this.memberGameAccount = memberGameAccount;
    }
}
