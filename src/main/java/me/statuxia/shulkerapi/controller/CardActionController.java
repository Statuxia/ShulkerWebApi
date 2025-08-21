package me.statuxia.shulkerapi.controller;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.CardOperationType;
import me.statuxia.shulkerapi.processor.impl.card.BalanceProcessor;
import me.statuxia.shulkerapi.request.ChangeCardBalanceRequest;
import me.statuxia.shulkerapi.service.AccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static me.statuxia.shulkerapi.model.TokenAuthorityEnum.DEPOSIT_FUNDS_TO_CARD;
import static me.statuxia.shulkerapi.model.TokenAuthorityEnum.WITHDRAW_FUNDS_FROM_CARD;


@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class CardActionController extends CardController {

    public static final String DEPOSIT = "/deposit";
    public static final String WITHDRAW = "/withdraw";

    @Autowired
    public CardActionController(
        TokenService tokenService,
        AccountService accountService, BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService
    ) {
        super(tokenService, accountService, bankCardDAO, cardProperties, operationProcessorService);
    }

    @PostMapping(value = DEPOSIT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Void> deposit(
        @RequestBody @Valid ChangeCardBalanceRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getBankCard(request, token, DEPOSIT_FUNDS_TO_CARD);

        if (card.isDisabled()) {
            return ResponseEntity.ok().build();
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
        final BankCard card = getBankCard(request, token, WITHDRAW_FUNDS_FROM_CARD);

        if (card.isDisabled()) {
            return ResponseEntity.ok().build();
        }

        final OperationData data = new OperationData()
            .addProcessor(BalanceProcessor.class)
            .addData(BalanceProcessor.CARD, card)
            .addData(BalanceProcessor.OPERATION, CardOperationType.WITHDRAW)
            .addData(BalanceProcessor.VALUE, request.getFunds());
        getOperationProcessorService().process(data);

        return ResponseEntity.ok().build();
    }
}
