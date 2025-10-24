package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.CardOperationType;
import me.statuxia.shulkerapi.model.CardType;
import me.statuxia.shulkerapi.processor.impl.card.BalanceProcessor;
import me.statuxia.shulkerapi.processor.impl.card.TransferFundsProcessor;
import me.statuxia.shulkerapi.request.ChangeCardBalanceRequest;
import me.statuxia.shulkerapi.request.TransferFundsRequest;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.service.impl.MessageService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.CardActionControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.card.*;
import me.statuxia.shulkerapi.swagger.controller.funds.AmountGreaterZeroOperation;
import me.statuxia.shulkerapi.utils.AdminCardHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static me.statuxia.shulkerapi.model.TokenAuthorityEnum.*;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class CardActionController extends CardController {

    public static final String DEPOSIT = "/deposit";
    public static final String WITHDRAW = "/withdraw";
    public static final String TRANSFER = "/transfer";

    private final CardActionController controller;

    @Autowired
    public CardActionController(
        TokenService tokenService,
        GameAccountService gameAccountService, BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService,
        CardHistoryService cardHistoryService, MessageService messageService
    ) {
        super(
            tokenService, gameAccountService, bankCardDAO, cardProperties,
            operationProcessorService, cardHistoryService, messageService
        );
        this.controller = this;
    }

    @RequiredAuthority(requireAll = DEPOSIT_FUNDS_TO_CARD)
    @PostMapping(value = DEPOSIT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CardActionControllerOperation.Deposit
    @CardDisabledOperation
    @UnknownCardOperation
    @UnknownAccountOperation
    @AmountGreaterZeroOperation
    @UnsupportedForAdminCardOperation
    public ResponseEntity<Void> deposit(
        @RequestBody @Valid ChangeCardBalanceRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, DEPOSIT_FUNDS_TO_CARD);
        AdminCardHelper.unsupportedForAdminCard(card);

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

    @RequiredAuthority(requireAll = WITHDRAW_FUNDS_FROM_CARD)
    @PostMapping(value = WITHDRAW, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CardDisabledOperation
    @UnknownCardOperation
    @InvalidPinOperation
    @CardActionControllerOperation.Withdraw
    @UnknownAccountOperation
    @AmountGreaterZeroOperation
    public ResponseEntity<Void> withdraw(
        @RequestBody @Valid ChangeCardBalanceRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, WITHDRAW_FUNDS_FROM_CARD);
        if (CardType.ADMIN.equals(card.getType())
            && !getTokenService().hasAuthority(token.token(), ADMIN_CARD_WITHDRAW)
        ) {
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

    @PostMapping(value = TRANSFER, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CardDisabledOperation
    @ReceiverCardDisabledOperation
    @UnknownCardOperation
    @InvalidPinOperation
    @CardActionControllerOperation.Transfer
    @UnknownAccountOperation
    @AmountGreaterZeroOperation
    @UnsupportedForAdminCardOperation
    @SameCardReceiverOperation
    public ResponseEntity<Void> transfer(
        @RequestBody @Valid TransferFundsRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, TRANSFER_FUNDS_FROM_CARD);
        AdminCardHelper.unsupportedForAdminCard(card);

        final BankCard receiverCard = getBankCardDAO().findByNumber(request.getReceiverCard())
            .orElseThrow(() -> CardException.UNKNOWN_RECEIVER_CARD);

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        if (receiverCard.isDisabled()) {
            throw CardException.RECEIVER_CARD_DISABLED;
        }

        if (card.getPin() == null || !Objects.equals(card.getPin(), request.getPin())) {
            throw CardException.INVALID_PIN;
        }

        final OperationData data = new OperationData()
            .addProcessor(TransferFundsProcessor.class)
            .addData(TransferFundsProcessor.MESSAGE, request.getMessage() == null ? "" : request.getMessage())
            .addData(TransferFundsProcessor.CARD, card)
            .addData(TransferFundsProcessor.RECEIVER, receiverCard)
            .addData(TransferFundsProcessor.VALUE, request.getFunds());
        getOperationProcessorService().process(data);

        return ResponseEntity.ok().build();
    }

    public CardActionController getController() {
        return controller;
    }
}
