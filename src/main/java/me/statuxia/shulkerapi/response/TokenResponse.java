package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {

    private String token;
    private Long accountId;
    private TokenLimitationResponse limitation;
    private List<TokenAuthorityEnum> authorities;

    public String getToken() {
        return token;
    }

    public TokenResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public Long getAccountId() {
        return accountId;
    }

    public TokenResponse setAccountId(Long accountId) {
        this.accountId = accountId;
        return this;
    }

    public TokenLimitationResponse getLimitation() {
        return limitation;
    }

    public TokenResponse setLimitation(TokenLimitationResponse limitation) {
        this.limitation = limitation;
        return this;
    }

    public List<TokenAuthorityEnum> getAuthorities() {
        return authorities;
    }

    public TokenResponse setAuthorities(List<TokenAuthorityEnum> authorities) {
        this.authorities = authorities;
        return this;
    }
}
