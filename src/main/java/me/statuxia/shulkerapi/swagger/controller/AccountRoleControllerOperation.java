package me.statuxia.shulkerapi.swagger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.statuxia.shulkerapi.response.AccountRoleResponseItem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class AccountRoleControllerOperation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Получение ролей<br>Авторити: ACCOUNT_ROLE_GET_FOR_BATCH",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(
                array = @ArraySchema(
                    schema = @Schema(implementation = AccountRoleResponseItem.class)
                )
            )
        )
    )
    public @interface GetForBatch {

    }
}
