package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardGetRequest extends PaginationRequest {

    @Pattern(regexp = "\\d{4} \\d{4}")
    protected String cardNumber;

    @Nullable
    protected String gameAccount;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Nullable
    public String getGameAccount() {
        return gameAccount;
    }

    public void setGameAccount(@Nullable String gameAccount) {
        this.gameAccount = gameAccount;
    }
}
