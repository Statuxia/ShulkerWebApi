package me.statuxia.shulkerapi.request;

import me.statuxia.shulkerapi.aware.ActionByAware;

public class ChangeCardStateRequest extends BaseCardRequest implements ActionByAware {

    protected String actionBy;

    @Override
    public String getActionBy() {
        return actionBy;
    }

    @Override
    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }
}
