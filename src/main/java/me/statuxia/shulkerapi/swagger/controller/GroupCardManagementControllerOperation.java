package me.statuxia.shulkerapi.swagger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class GroupCardManagementControllerOperation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Изменение настройки групповой карты<br>"
            + "Авторити: UPDATE_GROUP_CARD_SETTING",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema())
        )
    )
    public @interface UpdateSetting {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Добавление участника в групповую карту<br>"
            + "Авторити: MANAGE_GROUP_CARD_MEMBERS",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema())
        )
    )
    public @interface AddMember {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Удаление участника из групповой карты<br>"
            + "Авторити: MANAGE_GROUP_CARD_MEMBERS",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema())
        )
    )
    public @interface RemoveMember {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
        description = "Изменение PIN участника групповой карты<br>"
            + "Авторити: MANAGE_GROUP_CARD_MEMBER",
        responses = @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(schema = @Schema())
        )
    )
    public @interface UpdateMemberPin {
    }
}
