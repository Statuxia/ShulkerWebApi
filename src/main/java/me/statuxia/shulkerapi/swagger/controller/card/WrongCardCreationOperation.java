package me.statuxia.shulkerapi.swagger.controller.card;

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
        responseCode = "1213", description = "Создание карты с таким типом невозможно",
        content = @Content(schema = @Schema(implementation = ApiExceptionResponse.class))
    )
})
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WrongCardCreationOperation {
}
