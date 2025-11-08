package me.statuxia.shulkerapi.request;

public class CreateTwinkRequest {

    protected String paidAccount;
    protected String twinkName;

    public String getPaidAccount() {
        return paidAccount;
    }

    public void setPaidAccount(String paidAccount) {
        this.paidAccount = paidAccount;
    }

    public String getTwinkName() {
        return twinkName;
    }

    public void setTwinkName(String twinkName) {
        this.twinkName = twinkName;
    }
}
