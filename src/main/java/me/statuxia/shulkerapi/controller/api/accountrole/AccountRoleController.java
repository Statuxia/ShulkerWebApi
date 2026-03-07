package me.statuxia.shulkerapi.controller.api.accountrole;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.controller.api.Controller;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.AccountRoleRequest;
import me.statuxia.shulkerapi.response.AccountRoleResponseItem;
import me.statuxia.shulkerapi.service.AccountRoleService;
import me.statuxia.shulkerapi.swagger.AuthorityOperation;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.AccountRoleControllerOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Transactional
@RequestMapping(value = AccountRoleController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Tag(name = "AccountRole", description = "АккаунтоРолевые эндпоинты")
public class AccountRoleController implements Controller {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String PREFIX = "/api/v1/account-role";
    public static final String GET_FOR_BATCH_PATH = "/get-for-batch";

    private final AccountRoleService discordRoleService;

    @Autowired
    public AccountRoleController(AccountRoleService discordRoleService) {
        this.discordRoleService = discordRoleService;
    }

    @PostMapping(value = GET_FOR_BATCH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiredAuthority(requireAll = TokenAuthorityEnum.ACCOUNT_ROLE_GET_FOR_BATCH)
    @UnknownAccountOperation
    @AuthorityOperation
    @AccountRoleControllerOperation.GetForBatch
    public ResponseEntity<List<AccountRoleResponseItem>> getForBatch(
        @AuthData TokenData token,
        @RequestBody AccountRoleRequest request
    ) {
        return ResponseEntity.ok(discordRoleService.getRoles(request));
    }
}
