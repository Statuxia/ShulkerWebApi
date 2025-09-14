package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankCardItem {

    protected Long id;
    protected String cardNumber;
    protected String gameAccount;
    protected Long currency;
    protected Long createTime;
    protected Boolean disabled;
    protected Long disabledTime;

    public Long getId() {
        return id;
    }

    public BankCardItem setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public BankCardItem setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public String getGameAccount() {
        return gameAccount;
    }

    public BankCardItem setGameAccount(String gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }

    public Long getCurrency() {
        return currency;
    }

    public BankCardItem setCurrency(Long currency) {
        this.currency = currency;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public BankCardItem setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public BankCardItem setDisabled(Boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public Long getDisabledTime() {
        return disabledTime;
    }

    public BankCardItem setDisabledTime(Long disabledTime) {
        this.disabledTime = disabledTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BankCardItem that = (BankCardItem) o;
        return Objects.equals(id, that.id)
            && Objects.equals(cardNumber, that.cardNumber)
            && Objects.equals(gameAccount, that.gameAccount)
            && Objects.equals(currency, that.currency)
            && Objects.equals(disabled, that.disabled)
            && Objects.equals(disabledTime, that.disabledTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardNumber, gameAccount, currency, disabled, disabledTime);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BankCardItem{");
        sb.append("id=").append(id);
        sb.append(", cardNumber='").append(cardNumber).append('\'');
        sb.append(", gameAccount='").append(gameAccount).append('\'');
        sb.append(", currency=").append(currency);
        sb.append(", createTime=").append(createTime);
        sb.append(", disabled=").append(disabled);
        sb.append(", disabledTime=").append(disabledTime);
        sb.append('}');
        return sb.toString();
    }
}
