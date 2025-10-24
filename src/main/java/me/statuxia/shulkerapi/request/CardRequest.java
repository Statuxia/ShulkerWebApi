package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.statuxia.shulkerapi.aware.CardNumberAware;
import me.statuxia.shulkerapi.aware.GameAccountAware;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface CardRequest extends CardNumberAware, GameAccountAware {
    @Override
    String getGameAccount();

    @Override
    void setGameAccount(String gameAccount);

    @Override
    String getCardNumber();

    @Override
    void setCardNumber(String cardNumber);
}
