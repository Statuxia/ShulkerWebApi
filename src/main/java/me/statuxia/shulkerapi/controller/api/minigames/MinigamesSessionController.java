package me.statuxia.shulkerapi.controller.api.minigames;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.MinigamesJoinServerRequest;
import me.statuxia.shulkerapi.request.MinigamesLeftServerRequest;
import me.statuxia.shulkerapi.service.MinigamesSessionService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.minigames.MinigamesSessionControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.minigames.SessionNotStartedOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = MinigamesSessionController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Transactional
@Tag(name = "Minigames", description = "Контроллер сессий мини-игр")
public class MinigamesSessionController extends MinigamesController {

    public static final String PREFIX = MinigamesController.PREFIX + "/session";
    public static final String JOIN = "/join";
    public static final String LEFT = "/left";

    private final MinigamesSessionService minigamesSessionService;

    @Autowired
    public MinigamesSessionController(MinigamesSessionService minigamesSessionService) {
        this.minigamesSessionService = minigamesSessionService;
    }

    @UnknownAccountOperation
    @SessionNotStartedOperation
    @RequiredAuthority(requireAll = TokenAuthorityEnum.MINIGAMES_SESSION)
    @PostMapping(value = JOIN, produces = MediaType.APPLICATION_JSON_VALUE)
    @MinigamesSessionControllerOperation.Join
    public ResponseEntity<Void> join(
        @AuthData TokenData token,
        @RequestBody @Valid MinigamesJoinServerRequest request
    ) {
        minigamesSessionService.joinServer(request);
        return ResponseEntity.ok().build();
    }

    @UnknownAccountOperation
    @SessionNotStartedOperation
    @RequiredAuthority(requireAll = TokenAuthorityEnum.MINIGAMES_SESSION)
    @PostMapping(value = LEFT, produces = MediaType.APPLICATION_JSON_VALUE)
    @MinigamesSessionControllerOperation.Left
    public ResponseEntity<Void> left(
        @AuthData TokenData token,
        @RequestBody @Valid MinigamesLeftServerRequest request
    ) {
        minigamesSessionService.leftServer(request);
        return ResponseEntity.ok().build();
    }
}
