package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidatePinRequest implements CardRequest {

    @Pattern(regexp = "\\d{4} \\d{4}")
    protected String cardNumber;

    @NotNull
    @NotEmpty
    protected String gameAccount;

    @Pattern(regexp = "\\d{4}")
    protected String pin;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
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
}
