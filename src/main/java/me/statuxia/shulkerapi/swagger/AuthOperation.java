package me.statuxia.shulkerapi.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.statuxia.shulkerapi.response.ApiExceptionResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiResponses(value = {
    @ApiResponse(
        responseCode = "1101", description = "Неизвестный токен",
        content = @Content(schema = @Schema(implementation = ApiExceptionResponse.class))
    ),
    @ApiResponse(
        responseCode = "1102", description = "Достигнут лимит запросов",
        content = @Content(schema = @Schema(implementation = ApiExceptionResponse.class))
    ),
    @ApiResponse(
        responseCode = "1103", description = "Доступ запрещен",
        content = @Content(schema = @Schema(implementation = ApiExceptionResponse.class))
    ),
    @ApiResponse(
        responseCode = "1104", description = "Необходима переавторизация",
        content = @Content(schema = @Schema(implementation = ApiExceptionResponse.class))
    ),
    @ApiResponse(
        responseCode = "1105", description = "Токен не настроен до конца (Лимиты)",
        content = @Content(schema = @Schema(implementation = ApiExceptionResponse.class))
    )
})
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthOperation {
}
