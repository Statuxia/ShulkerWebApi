package me.statuxia.shulkerapi.controller;

import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.CardRequest;
import me.statuxia.shulkerapi.service.AccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public abstract class CardController implements AuthController {

    public static final String PREFIX = "/api/v1/card";

    private final TokenService tokenService;
    private final AccountService accountService;
    private final BankCardDAO bankCardDAO;
    private final CardProperties cardProperties;
    private final OperationProcessorService operationProcessorService;

    @Autowired
    protected CardController(
        TokenService tokenService,
        AccountService accountService,
        BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService
    ) {
        this.tokenService = tokenService;
        this.accountService = accountService;
        this.bankCardDAO = bankCardDAO;
        this.cardProperties = cardProperties;
        this.operationProcessorService = operationProcessorService;
    }

    protected BankCard getBankCard(CardRequest request, TokenData token, TokenAuthorityEnum updatePinCode) {
        final Optional<BankCard> optCard = getBankCardDAO().findByNumber(request.getCardNumber());
        if (optCard.isEmpty()) {
            throw CardException.UNKNOWN_CARD;
        }

        final BankCard card = optCard.get();

        if (!getTokenService().hasAuthority(token.token(), updatePinCode)) {
            getAccountService().validateOwned(token, card.getOwner());
        }

        return card;
    }

    public TokenService getTokenService() {
        return tokenService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public BankCardDAO getBankCardDAO() {
        return bankCardDAO;
    }

    public CardProperties getCardProperties() {
        return cardProperties;
    }

    public OperationProcessorService getOperationProcessorService() {
        return operationProcessorService;
    }
}
