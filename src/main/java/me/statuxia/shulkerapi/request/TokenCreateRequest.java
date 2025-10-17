package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenCreateRequest {

    private Long accountId;
    private Long rateLimit;
    private Long rateResetSeconds;
    private List<TokenAuthorityEnum> authorities = List.of();

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(Long rateLimit) {
        this.rateLimit = rateLimit;
    }

    public Long getRateResetSeconds() {
        return rateResetSeconds;
    }

    public void setRateResetSeconds(Long rateResetSeconds) {
        this.rateResetSeconds = rateResetSeconds;
    }

    public List<TokenAuthorityEnum> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<TokenAuthorityEnum> authorities) {
        this.authorities = authorities;
    }
}
