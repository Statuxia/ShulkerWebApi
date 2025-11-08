package me.statuxia.shulkerapi.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("api")
public class RootProperties {

    protected boolean linkPaidAccount = true;

    public boolean isLinkPaidAccount() {
        return linkPaidAccount;
    }

    public void setLinkPaidAccount(boolean linkPaidAccount) {
        this.linkPaidAccount = linkPaidAccount;
    }
}
