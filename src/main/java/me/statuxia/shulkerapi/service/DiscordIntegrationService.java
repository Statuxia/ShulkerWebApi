package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.response.DiscordAccessTokenResponse;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;

public interface DiscordIntegrationService {

    HttpHandler.HttpResponse<DiscordAccessTokenResponse> getAccessToken(String code);

    HttpHandler.HttpResponse<DiscordIdentityResponse> getUserInfo(String accessToken);

    HttpHandler.HttpResponse<DiscordAccessTokenResponse> refreshToken(String refreshToken);
}
