package me.statuxia.shulkerapi.configuration.properties;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;

import java.util.Set;

public class DiscordOAuthProperties {

    private final OAuth2ClientProperties.Registration registration;
    private final String tokenUri;
    private final String userInfoUri;

    public DiscordOAuthProperties(
        OAuth2ClientProperties.Registration registration,
        String tokenUri, String userInfoUri
    ) {
        this.registration = registration;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
    }

    public String getClientId() {
        return registration.getClientId();
    }

    public String getClientSecret() {
        return registration.getClientSecret();
    }

    public String getRedirectUri() {
        return registration.getRedirectUri();
    }

    public Set<String> getScope() {
        return registration.getScope();
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }
}
