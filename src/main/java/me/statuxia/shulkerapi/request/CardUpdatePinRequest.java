package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardUpdatePinRequest extends CardRequest {

    @Pattern(regexp = "\\d{4}")
    protected String pin;

    @Pattern(regexp = "\\d{4}")
    protected String newPin;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }
}
