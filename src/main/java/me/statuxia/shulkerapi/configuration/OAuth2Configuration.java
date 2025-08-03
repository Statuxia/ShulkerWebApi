package me.statuxia.shulkerapi.configuration;

import me.statuxia.shulkerapi.configuration.properties.DiscordOAuthProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2Configuration {

    @Value("${spring.security.oauth2.client.provider.discord.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.provider.discord.user-info-uri}")
    private String userInfoUri;

    @Bean
    public OAuth2ClientProperties properties() {
        return new OAuth2ClientProperties();
    }

    @Bean
    public DiscordOAuthProperties discordOAuthProperties() {
        final OAuth2ClientProperties.Registration registration = properties().getRegistration().get("discord");
        if (registration == null) {
            throw new IllegalStateException("invalid discord configuration");
        }

        return new DiscordOAuthProperties(registration, tokenUri, userInfoUri);
    }
}
