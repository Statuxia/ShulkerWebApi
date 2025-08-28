package me.statuxia.shulkerapi.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("session.token")
public class SessionTokenProperties {

    private Integer daysForDisable = 7;

    public Integer getDaysForDisable() {
        return daysForDisable;
    }

    public void setDaysForDisable(Integer daysForDisable) {
        this.daysForDisable = daysForDisable;
    }
}
