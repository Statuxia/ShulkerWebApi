package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import me.statuxia.shulkerapi.model.CardStyleType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardStyleRequest extends CardRequest {

    @NotNull
    private CardStyleType newStyle;

    @Pattern(regexp = "\\d{4}")
    private String pin;

    public CardStyleType getNewStyle() {
        return newStyle;
    }

    public void setNewStyle(CardStyleType newStyle) {
        this.newStyle = newStyle;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
