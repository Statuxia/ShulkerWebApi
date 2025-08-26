package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.CardOperationType;
import me.statuxia.shulkerapi.processor.impl.card.BalanceProcessor;
import me.statuxia.shulkerapi.request.ChangeCardBalanceRequest;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static me.statuxia.shulkerapi.model.TokenAuthorityEnum.DEPOSIT_FUNDS_TO_CARD;
import static me.statuxia.shulkerapi.model.TokenAuthorityEnum.WITHDRAW_FUNDS_FROM_CARD;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class CardActionController extends CardController {

    public static final String DEPOSIT = "/deposit";
    public static final String WITHDRAW = "/withdraw";

    protected CardActionController controller;

    @Autowired
    public CardActionController(
        TokenService tokenService,
        GameAccountService gameAccountService, BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService
    ) {
        super(tokenService, gameAccountService, bankCardDAO, cardProperties, operationProcessorService);
    }

    @PostMapping(value = DEPOSIT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Void> deposit(
        @RequestBody @Valid ChangeCardBalanceRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, DEPOSIT_FUNDS_TO_CARD);

        if (!getGameAccountService().getGameAccount(request.getGameAccount()).equals(card.getGameAccount())) {
            throw CardException.UNKNOWN_CARD;
        }

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        final OperationData data = new OperationData()
            .addProcessor(BalanceProcessor.class)
            .addData(BalanceProcessor.CARD, card)
            .addData(BalanceProcessor.OPERATION, CardOperationType.DEPOSIT)
            .addData(BalanceProcessor.VALUE, request.getFunds());
        getOperationProcessorService().process(data);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = WITHDRAW, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Void> withdraw(
        @RequestBody @Valid ChangeCardBalanceRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, WITHDRAW_FUNDS_FROM_CARD);

        if (!getGameAccountService().getGameAccount(request.getGameAccount()).equals(card.getGameAccount())) {
            throw CardException.UNKNOWN_CARD;
        }

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        if (card.getPin() == null || !Objects.equals(card.getPin(), request.getPin())) {
            throw CardException.INVALID_PIN;
        }

        final OperationData data = new OperationData()
            .addProcessor(BalanceProcessor.class)
            .addData(BalanceProcessor.CARD, card)
            .addData(BalanceProcessor.OPERATION, CardOperationType.WITHDRAW)
            .addData(BalanceProcessor.VALUE, request.getFunds());
        getOperationProcessorService().process(data);

        return ResponseEntity.ok().build();
    }

    public CardActionController getController() {
        return controller;
    }

    @Autowired
    @Lazy
    public void setController(CardActionController controller) {
        this.controller = controller;
    }
}
