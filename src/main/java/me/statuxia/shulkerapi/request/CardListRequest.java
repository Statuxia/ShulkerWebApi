package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import me.statuxia.shulkerapi.model.CardType;
import org.joda.time.DateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardListRequest extends PaginationRequest implements CardRequest {

    @Pattern(regexp = "\\d{4} \\d{4}")
    protected String cardNumber;

    @NotNull
    @NotEmpty
    protected String gameAccount;

    protected CardType cardType;

    protected DateTime createFrom;
    protected DateTime createTo;

    @Override
    public String getGameAccount() {
        return gameAccount;
    }

    @Override
    public void setGameAccount(String gameAccount) {
        this.gameAccount = gameAccount;
    }

    @Override
    public String getCardNumber() {
        return cardNumber;
    }

    @Override
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public DateTime getCreateFrom() {
        return createFrom;
    }

    public void setCreateFrom(DateTime createFrom) {
        this.createFrom = createFrom;
    }

    public DateTime getCreateTo() {
        return createTo;
    }

    public void setCreateTo(DateTime createTo) {
        this.createTo = createTo;
    }
}
