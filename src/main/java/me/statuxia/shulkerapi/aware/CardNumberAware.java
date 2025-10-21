package me.statuxia.shulkerapi.aware;

import jakarta.validation.constraints.Pattern;

public interface CardNumberAware {

    @Pattern(regexp = "\\d{4} \\d{4}")
    String getCardNumber();

    void setCardNumber(@Pattern(regexp = "\\d{4} \\d{4}") String cardNumber);
}
