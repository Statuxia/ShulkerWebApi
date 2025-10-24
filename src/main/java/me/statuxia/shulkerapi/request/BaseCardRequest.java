package me.statuxia.shulkerapi.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class BaseCardRequest implements CardRequest {

    @Pattern(regexp = "\\d{4} \\d{4}")
    protected String cardNumber;

    @NotNull
    @NotEmpty
    protected String gameAccount;

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
