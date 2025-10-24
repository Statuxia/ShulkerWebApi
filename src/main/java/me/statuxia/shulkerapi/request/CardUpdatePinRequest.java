package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import me.statuxia.shulkerapi.aware.ActionByAware;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardUpdatePinRequest implements CardRequest, ActionByAware {

    @Pattern(regexp = "\\d{4} \\d{4}")
    protected String cardNumber;

    @NotNull
    @NotEmpty
    protected String gameAccount;

    @Pattern(regexp = "\\d{4}")
    @NotNull
    protected String pin;

    @Pattern(regexp = "\\d{4}")
    @NotNull
    protected String newPin;

    protected String actionBy;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }

    @Override
    public String getCardNumber() {
        return cardNumber;
    }

    @Override
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public String getGameAccount() {
        return gameAccount;
    }

    @Override
    public void setGameAccount(String gameAccount) {
        this.gameAccount = gameAccount;
    }

    @Override
    public String getActionBy() {
        return actionBy;
    }

    @Override
    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }
}
