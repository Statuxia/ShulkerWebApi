package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordAccessTokenResponse {

    @JsonProperty("access_token")
    protected String accessToken;

    @JsonProperty("refresh_token")
    protected String refreshToken;

    @JsonProperty("expires_in")
    protected Long expireIn;

    public String getAccessToken() {
        return accessToken;
    }

    public DiscordAccessTokenResponse setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public DiscordAccessTokenResponse setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public Long getExpireIn() {
        return expireIn;
    }

    public DiscordAccessTokenResponse setExpireIn(Long expireIn) {
        this.expireIn = expireIn;
        return this;
    }
}
