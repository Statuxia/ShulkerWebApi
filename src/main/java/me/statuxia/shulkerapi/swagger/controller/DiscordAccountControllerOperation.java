package me.statuxia.shulkerapi.swagger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.statuxia.shulkerapi.response.DiscordAccountUnlinkResponse;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class DiscordAccountControllerOperation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Получение данных о профиле",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = DiscordIdentityResponse.class))
        )
    )
    public @interface Get {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Отвязка игрового аккаунта от Discord аккаунта",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = DiscordAccountUnlinkResponse.class))
        )
    )
    public @interface Unlink {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Привязка игрового аккаунта к Discord аккаунту",
        responses = @ApiResponse(
            responseCode = "200", description = "OK"
        )
    )
    public @interface Link {
    }
}
