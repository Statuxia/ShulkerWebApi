package me.statuxia.shulkerapi.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("card")
public class CardProperties {

    private Integer maxDirectCards = 3;
    private Integer newDirectCardPayment = 64;
    private Integer newGroupCardPayment = 128;

    public Integer getMaxDirectCards() {
        return maxDirectCards;
    }

    public void setMaxDirectCards(Integer maxDirectCards) {
        this.maxDirectCards = maxDirectCards;
    }

    public Integer getNewDirectCardPayment() {
        return newDirectCardPayment;
    }

    public void setNewDirectCardPayment(Integer newDirectCardPayment) {
        this.newDirectCardPayment = newDirectCardPayment;
    }

    public Integer getNewGroupCardPayment() {
        return newGroupCardPayment;
    }

    public void setNewGroupCardPayment(Integer newGroupCardPayment) {
        this.newGroupCardPayment = newGroupCardPayment;
    }
}
