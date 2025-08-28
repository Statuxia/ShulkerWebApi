package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.processor.impl.card.BalanceProcessor;
import me.statuxia.shulkerapi.request.CardCreateRequest;
import me.statuxia.shulkerapi.request.CardRequest;
import me.statuxia.shulkerapi.request.CardUpdatePinRequest;
import me.statuxia.shulkerapi.response.CardResponse;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.CardManagementControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.card.*;
import me.statuxia.shulkerapi.swagger.controller.funds.AmountGreaterZeroOperation;
import me.statuxia.shulkerapi.swagger.controller.funds.NotEnoughFundsOperation;
import me.statuxia.shulkerapi.utils.CardNumberGenerator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    protected CardManagementController controller;

    @Autowired
    public CardManagementController(
        TokenService tokenService, GameAccountService gameAccountService, BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService
    ) {
        super(tokenService, gameAccountService, bankCardDAO, cardProperties, operationProcessorService);
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
        bankCard.setGameAccount(gameAccount);
        bankCard.setPin(request.getPin());

        final Long totalCards = getBankCardDAO().countByGameAccountAndType(gameAccount, request.getType());
        if (CardType.DIRECT.equals(request.getType()) && totalCards >= getCardProperties().getMaxDirectCards()) {
            throw CardException.TOO_MANY_DIRECT_CARDS;
        }

        if (totalCards >= 1) {
            final String number = request.getPaymentCardNumber();
            if (!StringUtils.hasText(number)) {
                throw CardException.UNKNOWN_PAYMENT_CARD;
            }

            final Optional<BankCard> paymentCard = getBankCardDAO().findByNumberAndGameAccount(number, gameAccount);
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

            final OperationData data = new OperationData()
                .addProcessor(BalanceProcessor.class)
                .addData(BalanceProcessor.CARD, card)
                .addData(BalanceProcessor.OPERATION, CardOperationType.WITHDRAW)
                .addData(BalanceProcessor.VALUE, getCardProperties().getNewDirectCardPayment());
            getOperationProcessorService().process(data);
        }

        getBankCardDAO().save(bankCard);

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

        if (!card.getPin().equals(request.getPin())) {
            throw CardException.INVALID_PIN;
        }

        card.setPin(request.getNewPin());
        getBankCardDAO().save(card);

        return ResponseEntity.ok().build();
    }

    @RequiredAuthority(requireAll = DISABLE_BANK_CARD)
    @PutMapping(value = DISABLE_CARD, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CardManagementControllerOperation.Disable
    @UnknownAccountOperation
    @UnknownCardOperation
    @CardDisabledOperation
    public ResponseEntity<Void> disable(
        @RequestBody @Valid CardRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, null);

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        card.setDisabled(true);
        card.setDisabledTime(DateTime.now());

        getBankCardDAO().save(card);

        return ResponseEntity.ok().build();
    }

    @RequiredAuthority(requireAll = ENABLE_BANK_CARD)
    @PutMapping(value = ENABLE_CARD, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CardManagementControllerOperation.Enable
    @UnknownAccountOperation
    @UnknownCardOperation
    @CardDisabledOperation
    public ResponseEntity<Void> enable(
        @RequestBody @Valid CardRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, null);

        if (!card.isDisabled()) {
            throw CardException.CARD_ENABLED;
        }

        card.setDisabled(false);
        card.setDisabledTime(null);

        getBankCardDAO().save(card);

        return ResponseEntity.ok().build();
    }

    public CardManagementController getController() {
        return controller;
    }

    @Autowired
    @Lazy
    public void setController(CardManagementController controller) {
        this.controller = controller;
    }
}
