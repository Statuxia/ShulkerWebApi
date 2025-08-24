package me.statuxia.shulkerapi.controller;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.request.CardGameAccountRequest;
import me.statuxia.shulkerapi.request.CardRequest;
import me.statuxia.shulkerapi.response.BankCardItem;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class ViewCardController extends CardController {

    public static final String GET = "/get";
    public static final String LIST = "/list";

    @Autowired
    public ViewCardController(
        TokenService tokenService,
        GameAccountService gameAccountService,
        BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService
    ) {
        super(tokenService, gameAccountService, bankCardDAO, cardProperties, operationProcessorService);
    }

    @PostMapping(value = LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BankCardItem>> list(
        @AuthData TokenData token,
        @RequestBody @Valid CardGameAccountRequest request
    ) {
        return ResponseEntity.ok(getBankCards(request, token).stream().map(this::buildItem).toList());
    }

    @PostMapping(value = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BankCardItem> get(
        @AuthData TokenData token,
        @RequestBody @Valid CardRequest request
    ) {
        final BankCard card = getBankCard(request, token, null);
        return ResponseEntity.ok(buildItem(card));
    }

    private BankCardItem buildItem(BankCard card) {
        return new BankCardItem()
            .setId(card.getId())
            .setCardNumber(card.getNumber())
            .setGameAccount(card.getGameAccount() == null ? null : card.getGameAccount().getName())
            .setCurrency(card.getCurrency());
    }
}
