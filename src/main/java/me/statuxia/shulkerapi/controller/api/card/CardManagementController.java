package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.dto.search.impl.BankCardSearchDTO;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.processor.impl.card.BalanceProcessor;
import me.statuxia.shulkerapi.request.CardCreateRequest;
import me.statuxia.shulkerapi.request.CardRequest;
import me.statuxia.shulkerapi.request.CardUpdatePinRequest;
import me.statuxia.shulkerapi.response.CardResponse;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.service.impl.MessageService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.UnknownActionByAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.CardManagementControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.card.*;
import me.statuxia.shulkerapi.swagger.controller.funds.AmountGreaterZeroOperation;
import me.statuxia.shulkerapi.swagger.controller.funds.NotEnoughFundsOperation;
import me.statuxia.shulkerapi.utils.CardNumberGenerator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static me.statuxia.shulkerapi.model.TokenAuthorityEnum.*;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class CardManagementController extends CardController {

    public static final String CREATE = "/create";
    public static final String UPDATE_PIN = "/update-pin";
    public static final String DISABLE_CARD = "/disable";
    public static final String ENABLE_CARD = "/enable";

    private final CardManagementController controller;

    @Autowired
    public CardManagementController(
        TokenService tokenService, GameAccountService gameAccountService, BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService, CardHistoryService cardHistoryService,
        MessageService messageService
    ) {
        super(
            tokenService, gameAccountService, bankCardDAO, cardProperties,
            operationProcessorService, cardHistoryService, messageService
        );
        this.controller = this;
    }

    @PostMapping(value = CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @CardDisabledOperation
    @TooManyDirectCardsOperation
    @UnknownPaymentCardOperation
    @PaymentCardDisabledOperation
    @PaymentFromDirectOperation
    @AmountGreaterZeroOperation
    @NotEnoughFundsOperation
    @InvalidPinOperation
    @InvalidPaymentPinOperation
    @CardManagementControllerOperation.Create
    public ResponseEntity<CardResponse> createCard(
        @RequestBody @Valid CardCreateRequest request,
        @AuthData TokenData token
    ) {
        final GameAccount gameAccount = getGameAccountService().getGameAccount(
            token,
            request.getGameAccount(),
            TokenAuthorityEnum.CREATE_BANK_CARD
        );
        final BankCard bankCard = new BankCard();
        bankCard.setNumber(CardNumberGenerator.generate());
        bankCard.setType(request.getType());
        bankCard.setCreateTime(DateTime.now());
        bankCard.updatePatternSeed();
        bankCard.setGameAccount(gameAccount);
        bankCard.setPin(request.getPin());

        final BankCardSearchDTO dto = new BankCardSearchDTO()
            .setGameAccount(gameAccount)
            .setCardType(request.getType());
        final Long totalCards = getBankCardDAO().count(dto);
        if (CardType.DIRECT.equals(request.getType()) && totalCards >= getCardProperties().getMaxDirectCards()) {
            throw CardException.TOO_MANY_DIRECT_CARDS;
        }

        if (totalCards >= 1) {
            final String number = request.getPaymentCardNumber();
            if (!StringUtils.hasText(number)) {
                throw CardException.UNKNOWN_PAYMENT_CARD;
            }

            dto.setNumber(number);

            final Optional<BankCard> paymentCard = getBankCardDAO().find(dto);
            if (paymentCard.isEmpty()) {
                throw CardException.UNKNOWN_PAYMENT_CARD;
            }

            final BankCard card = paymentCard.get();
            if (card.isDisabled()) {
                throw CardException.PAYMENT_CARD_DISABLED;
            }

            if (!CardType.DIRECT.equals(card.getType())) {
                throw CardException.PAYMENT_FROM_DIRECT;
            }

            if (!card.getPin().equals(request.getPaymentCardPin())) {
                throw CardException.INVALID_PAYMENT_PIN;
            }

            final OperationData data = new OperationData()
                .addProcessor(BalanceProcessor.class)
                .addData(BalanceProcessor.CARD, card)
                .addData(BalanceProcessor.OPERATION, CardOperationType.WITHDRAW)
                .addData(BalanceProcessor.VALUE, getCardProperties().getNewDirectCardPayment());
            getOperationProcessorService().process(data);
        }

        getBankCardDAO().save(bankCard);
        getCardHistoryService().writeCreateCard(bankCard);

        final CardResponse response = new CardResponse();
        response.setNumber(bankCard.getNumber());

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = UPDATE_PIN, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownCardOperation
    @CardDisabledOperation
    @InvalidPinOperation
    @CardManagementControllerOperation.UpdatePin
    public ResponseEntity<Void> updatePin(
        @RequestBody @Valid CardUpdatePinRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, UPDATE_PIN_CODE);

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        final boolean isAdmin = getTokenService().hasAuthority(token.token(), UPDATE_PIN_CODE);

        if (!isAdmin && !card.getPin().equals(request.getPin())) {
            throw CardException.INVALID_PIN;
        }

        card.setPin(request.getNewPin());
        getBankCardDAO().save(card);
        getCardHistoryService().writeUpdatePin(card, isAdmin);

        return ResponseEntity.ok().build();
    }

    @RequiredAuthority(requireAll = DISABLE_BANK_CARD)
    @PutMapping(value = DISABLE_CARD, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CardManagementControllerOperation.Disable
    @UnknownAccountOperation
    @UnknownActionByAccountOperation
    @UnknownCardOperation
    @CardDisabledOperation
    public ResponseEntity<Void> disable(
        @RequestBody @Valid CardRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, null);
        final GameAccount actionBy = getGameAccountService().getActionGameAccount(request.getActionBy());

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        card.setDisabled(true);
        card.setDisabledTime(DateTime.now());


        getBankCardDAO().save(card);
        getCardHistoryService().writeDisable(card, actionBy, true);

        return ResponseEntity.ok().build();
    }

    @RequiredAuthority(requireAll = ENABLE_BANK_CARD)
    @PutMapping(value = ENABLE_CARD, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CardManagementControllerOperation.Enable
    @UnknownAccountOperation
    @UnknownActionByAccountOperation
    @UnknownCardOperation
    @CardDisabledOperation
    public ResponseEntity<Void> enable(
        @RequestBody @Valid CardRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, null);
        final GameAccount actionBy = getGameAccountService().getActionGameAccount(request.getActionBy());

        if (!card.isDisabled()) {
            throw CardException.CARD_ENABLED;
        }

        card.setDisabled(false);
        card.setDisabledTime(null);

        getBankCardDAO().save(card);
        getCardHistoryService().writeDisable(card, actionBy, false);

        return ResponseEntity.ok().build();
    }

    public CardManagementController getController() {
        return controller;
    }
}
