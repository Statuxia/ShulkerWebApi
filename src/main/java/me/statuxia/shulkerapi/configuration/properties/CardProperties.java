package me.statuxia.shulkerapi.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("card")
public class CardProperties {

    private Long maxDirectCards = 3L;
    private Long newDirectCardPayment = 64L;
    private Long newGroupCardPayment = 128L;

    public Long getMaxDirectCards() {
        return maxDirectCards;
    }

    public void setMaxDirectCards(Long maxDirectCards) {
        this.maxDirectCards = maxDirectCards;
    }

    public Long getNewDirectCardPayment() {
        return newDirectCardPayment;
    }

    public void setNewDirectCardPayment(Long newDirectCardPayment) {
        this.newDirectCardPayment = newDirectCardPayment;
    }

    public Long getNewGroupCardPayment() {
        return newGroupCardPayment;
    }

    public void setNewGroupCardPayment(Long newGroupCardPayment) {
        this.newGroupCardPayment = newGroupCardPayment;
    }
}
