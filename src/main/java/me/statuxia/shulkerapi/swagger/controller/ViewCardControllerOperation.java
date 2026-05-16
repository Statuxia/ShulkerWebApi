package me.statuxia.shulkerapi.swagger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.statuxia.shulkerapi.response.BankCardItem;
import me.statuxia.shulkerapi.response.BankCardPaginationResponse;
import me.statuxia.shulkerapi.response.CardTypeItem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ViewCardControllerOperation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Получение списка карт",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = BankCardPaginationResponse.class))
        )
    )
    public @interface List {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Получение карты",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = BankCardItem.class))
        )
    )
    public @interface Get {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Получение списка типов карт",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(
                array = @ArraySchema(
                    schema = @Schema(implementation = CardTypeItem.class)
                )
            )
        )
    )
    public @interface Types {
    }
}
