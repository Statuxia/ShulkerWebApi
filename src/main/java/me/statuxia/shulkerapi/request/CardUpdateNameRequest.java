package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.statuxia.shulkerapi.aware.ActionByAware;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardUpdateNameRequest extends BaseCardRequest implements ActionByAware {

    private String pin;
    private String name;
    private String actionBy;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getActionBy() {
        return actionBy;
    }

    @Override
    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }
}
