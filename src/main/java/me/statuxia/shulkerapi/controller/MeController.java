package me.statuxia.shulkerapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;
import me.statuxia.shulkerapi.response.MeGameAccountResponse;
import me.statuxia.shulkerapi.response.MeResponse;
import me.statuxia.shulkerapi.service.DiscordAccountService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.MeControllerOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = MeController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Tag(name = "Me", description = "Основная информация о профиле по сессионному токену")
public class MeController implements AuthController {

    public static final String PREFIX = "/api/v1/me";

    private final DiscordAccountService discordAccountService;
    private final GameAccountDAO gameAccountDAO;

    @Autowired
    public MeController(DiscordAccountService discordAccountService, GameAccountDAO gameAccountDAO) {
        this.discordAccountService = discordAccountService;
        this.gameAccountDAO = gameAccountDAO;
    }

    @GetMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @MeControllerOperation.Me
    @UnknownAccountOperation
    public ResponseEntity<MeResponse> me(@AuthData TokenData token) {
        if (!(token.source() instanceof DiscordAccount discordAccount)) {
            throw AccountException.UNKNOWN_ACCOUNT;
        }

        final List<MeGameAccountResponse> gameAccounts = gameAccountDAO.findByDiscordAccount(discordAccount).stream()
            .map(account -> {
                final MeGameAccountResponse gameAccountResponse = new MeGameAccountResponse();
                gameAccountResponse.setId(account.getId());
                gameAccountResponse.setName(account.getName());
                return gameAccountResponse;
            }).toList();
        final DiscordIdentityResponse identityResponse = discordAccountService.getByDiscord(discordAccount);

        return ResponseEntity.ok(
            new MeResponse()
                .setDiscord(identityResponse)
                .setGameAccounts(gameAccounts)
        );
    }
}
