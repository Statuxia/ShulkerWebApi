package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.CardStyleDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.request.CardStyleRequest;
import me.statuxia.shulkerapi.response.CardStyleItem;
import me.statuxia.shulkerapi.service.*;
import me.statuxia.shulkerapi.service.impl.MessageService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.CardStyleControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.card.*;
import me.statuxia.shulkerapi.swagger.controller.card.style.NotForPurchaseOperation;
import me.statuxia.shulkerapi.swagger.controller.card.style.UnknownStyleOperation;
import me.statuxia.shulkerapi.swagger.controller.funds.NotEnoughFundsOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class CardStyleController extends CardController {

    private final CardStyleDAO cardStyleDAO;
    private final BankCardService bankCardService;
    private final CardStyleController controller;

    public static final String TYPES = "/styles/types";
    public static final String CHANGE_STYLE = "/styles/change-style";

    @Autowired
    public CardStyleController(
        TokenService tokenService,
        GameAccountService gameAccountService,
        BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService,
        CardHistoryService cardHistoryService,
        MessageService messageService, CardStyleDAO cardStyleDAO,
        BankCardService bankCardService
    ) {
        super(
            tokenService,
            gameAccountService,
            bankCardDAO,
            cardProperties,
            operationProcessorService,
            cardHistoryService,
            messageService
        );
        this.cardStyleDAO = cardStyleDAO;
        this.bankCardService = bankCardService;
        this.controller = this;
    }

    @PutMapping(value = CHANGE_STYLE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @CardDisabledOperation
    @PaymentCardDisabledOperation
    @NotEnoughFundsOperation
    @InvalidPinOperation
    @NotForPurchaseOperation
    @UnknownStyleOperation
    @CardStyleControllerOperation.ChangeStyle
    public ResponseEntity<Void> changeStyle(
        @AuthData TokenData token,
        @RequestBody @Valid CardStyleRequest request
    ) {
        final BankCard card = controller.getBankCard(request, token, null);
        if (card.isDisabled()) {
            throw CardException.PAYMENT_CARD_DISABLED;
        }

        if (card.getPin() == null || !Objects.equals(card.getPin(), request.getPin())) {
            throw CardException.INVALID_PIN;
        }

        bankCardService.changeStyle(card, request.getNewStyle());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = TYPES, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CardStyleControllerOperation.Types
    public ResponseEntity<List<CardStyleItem>> types() {
        return ResponseEntity.ok(
            cardStyleDAO.findAll().stream().map(item -> new CardStyleItem(
                item.getType().name(),
                getMessageService().message(item.getType().name()),
                item.getCardType(),
                item.getPrice()
            )).toList()
        );
    }
}
