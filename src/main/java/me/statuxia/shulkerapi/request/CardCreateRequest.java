package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import me.statuxia.shulkerapi.model.CardType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardCreateRequest {

    @NotNull
    private CardType type;

    @Pattern(regexp = "\\d{4}")
    @NotNull
    private String pin;

    @Pattern(regexp = "\\d{4} \\d{4}")
    private String paymentCardNumber;

    @Pattern(regexp = "\\d{4}")
    private String paymentCardPin;

    @NotNull
    @NotEmpty
    protected String gameAccount;

    private String name;

    public String getGameAccount() {
        return gameAccount;
    }

    public void setGameAccount(String gameAccount) {
        this.gameAccount = gameAccount;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPaymentCardNumber() {
        return paymentCardNumber;
    }

    public void setPaymentCardNumber(String paymentCardNumber) {
        this.paymentCardNumber = paymentCardNumber;
    }

    public String getPaymentCardPin() {
        return paymentCardPin;
    }

    public void setPaymentCardPin(String paymentCardPin) {
        this.paymentCardPin = paymentCardPin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
