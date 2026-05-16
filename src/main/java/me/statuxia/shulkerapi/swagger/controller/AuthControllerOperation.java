package me.statuxia.shulkerapi.swagger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.statuxia.shulkerapi.response.AuthRefreshResponse;
import me.statuxia.shulkerapi.response.AuthValidateResponse;
import me.statuxia.shulkerapi.response.GameSessionIpResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class AuthControllerOperation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Валидация входа игрока<br>Авторити: AUTH_VALIDATE",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = AuthValidateResponse.class))
        )
    )
    public @interface Validate {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Получение списка новых сессий, по которым не было уведомлений<br>Авторити: AUTH_QUEUE",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = GameSessionIpResponse.class))
        )
    )
    public @interface Queue {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Изменение состояния сессии<br>Авторити: AUTH_CHANGE_STATE",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema())
        )
    )
    public @interface ChangeState {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Обновление сессии после входа<br>Авторити: AUTH_REFRESH",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = AuthRefreshResponse.class))
        )
    )
    public @interface Refresh {
    }
}
