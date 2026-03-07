package me.statuxia.shulkerapi.controller.api.role;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.controller.api.Controller;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.response.RoleResponseItem;
import me.statuxia.shulkerapi.service.RoleService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.RoleControllerOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Transactional
@RequestMapping(value = RoleController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Tag(name = "Role", description = "Ролевые эндпоинты")
public class RoleController implements Controller {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String PREFIX = "/api/v1/role";
    public static final String LIST = "/list";

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping(value = LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @UnknownAccountOperation
    @RoleControllerOperation.List
    public ResponseEntity<List<RoleResponseItem>> list(@AuthData TokenData token) {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
