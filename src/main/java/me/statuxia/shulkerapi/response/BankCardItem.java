package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankCardItem {

    protected Long id;
    protected String cardNumber;
    protected String gameAccount;
    protected Long currency;

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
}
