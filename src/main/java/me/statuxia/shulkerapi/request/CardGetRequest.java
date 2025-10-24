package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardGetRequest {

    @Pattern(regexp = "\\d{4} \\d{4}")
    protected String cardNumber;

    @Nullable
    @Schema(nullable = true, description = "Требуется если нет авторити GET_CARD_WITHOUT_REMOVE_DATA")
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
