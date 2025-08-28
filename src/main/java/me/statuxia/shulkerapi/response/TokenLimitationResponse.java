package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenLimitationResponse {

    private Long rateLimit;
    private Long rateResetSeconds;

    public Long getRateLimit() {
        return rateLimit;
    }

    public TokenLimitationResponse setRateLimit(Long rateLimit) {
        this.rateLimit = rateLimit;
        return this;
    }

    public Long getRateResetSeconds() {
        return rateResetSeconds;
    }

    public TokenLimitationResponse setRateResetSeconds(Long rateResetSeconds) {
        this.rateResetSeconds = rateResetSeconds;
        return this;
    }
}
