package me.statuxia.shulkerapi.swagger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.statuxia.shulkerapi.response.BankCardHistoryPaginationResponse;
import me.statuxia.shulkerapi.response.NamedItem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ViewCardHistoryControllerOperation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Получение списка истории карты",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = BankCardHistoryPaginationResponse.class))
        )
    )
    public @interface List {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Получение списка типов истории карт",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(
                array = @ArraySchema(
                    schema = @Schema(implementation = NamedItem.class)
                )
            )
        )
    )
    public @interface Types {
    }
}
