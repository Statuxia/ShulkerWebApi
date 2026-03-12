package me.statuxia.shulkerapi.swagger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.statuxia.shulkerapi.response.CanCreateTwinkResponse;
import me.statuxia.shulkerapi.response.GameAccountBalanceResponse;
import me.statuxia.shulkerapi.response.GameAccountResponse;
import me.statuxia.shulkerapi.response.NamedItem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class GameAccountControllerOperation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Получение аккаунта",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = GameAccountResponse.class))
        )
    )
    public @interface Get {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Создание аккаунта<br>Авторити: ADD_GAME_ACCOUNTS",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = GameAccountResponse.class))
        )
    )
    public @interface Create {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Изменение никнйма<br>Авторити: RENAME_GAME_ACCOUNTS",
        responses = @ApiResponse(responseCode = "200", description = "OK")
    )
    public @interface Rename {

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Возможно ли создать твинка<br>Авторити: CAN_CREATE_TWINK",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = CanCreateTwinkResponse.class))
        )
    )
    public @interface CanCreateTwink {

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Получение баланса",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = GameAccountBalanceResponse.class))
        )
    )
    public @interface BalanceGet {

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Получение типов баланса",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(
                array = @ArraySchema(
                    schema = @Schema(implementation = NamedItem.class)
                )
            )
        )
    )
    public @interface BalanceTypes {

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Изменение баланса\nАвторити: GAME_ACCOUNT_BALANCE_CHANGE",
        responses = @ApiResponse(responseCode = "200", description = "OK")
    )
    public @interface BalanceChange {

    }
}
