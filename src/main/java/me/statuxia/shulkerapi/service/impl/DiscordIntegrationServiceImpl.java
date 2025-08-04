package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.configuration.properties.DiscordOAuthProperties;
import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.response.DiscordAccessTokenResponse;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;
import me.statuxia.shulkerapi.service.DiscordIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class DiscordIntegrationServiceImpl implements DiscordIntegrationService {

    private final DiscordOAuthProperties properties;
    private final HttpHandler httpHandler;

    @Autowired
    public DiscordIntegrationServiceImpl(DiscordOAuthProperties properties, HttpHandler httpHandler) {
        this.properties = properties;
        this.httpHandler = httpHandler;
    }

    @Override
    public HttpHandler.HttpResponse<DiscordAccessTokenResponse> getAccessToken(String code) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final StringBuilder builder = new StringBuilder()
            .append("client_id=").append(getProperties().getClientId())
            .append("&client_secret=").append(getProperties().getClientSecret())
            .append("&grant_type=authorization_code")
            .append("&code=").append(code)
            .append("&redirect_uri=").append(getProperties().getRedirectUri());

        final HttpEntity<String> request = new HttpEntity<>(builder.toString(), headers);

        return getHttpHandler().post(getProperties().getTokenUri(), request, DiscordAccessTokenResponse.class);
    }

    @Override
    public HttpHandler.HttpResponse<DiscordIdentityResponse> getUserInfo(String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        final HttpEntity<String> request = new HttpEntity<>(headers);

        return getHttpHandler().get(getProperties().getUserInfoUri(), request, DiscordIdentityResponse.class);
    }

    @Override
    public HttpHandler.HttpResponse<DiscordAccessTokenResponse> refreshToken(String refreshToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final StringBuilder builder = new StringBuilder()
            .append("client_id=").append(getProperties().getClientId())
            .append("&client_secret=").append(getProperties().getClientSecret())
            .append("&grant_type=refresh_token")
            .append("&refresh_token=").append(refreshToken);

        final HttpEntity<String> request = new HttpEntity<>(builder.toString(), headers);

        return getHttpHandler().post(getProperties().getTokenUri(), request, DiscordAccessTokenResponse.class);
    }

    public DiscordOAuthProperties getProperties() {
        return properties;
    }

    public HttpHandler getHttpHandler() {
        return httpHandler;
    }
}
