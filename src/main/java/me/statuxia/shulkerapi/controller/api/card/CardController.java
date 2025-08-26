package me.statuxia.shulkerapi.controller.api.card;

import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.api.AuthController;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.CardRequest;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public abstract class CardController implements AuthController {

    public static final String PREFIX = "/api/v1/card";

    private final TokenService tokenService;
    private final GameAccountService gameAccountService;
    private final BankCardDAO bankCardDAO;
    private final CardProperties cardProperties;
    private final OperationProcessorService operationProcessorService;

    @Autowired
    protected CardController(
        TokenService tokenService,
        GameAccountService gameAccountService,
        BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService
    ) {
        this.tokenService = tokenService;
        this.gameAccountService = gameAccountService;
        this.bankCardDAO = bankCardDAO;
        this.cardProperties = cardProperties;
        this.operationProcessorService = operationProcessorService;
    }

    @Transactional
    public List<BankCard> getBankCards(CardRequest request, TokenData token) {
        final GameAccount gameAccount = getGameAccountService().getGameAccount(request.getGameAccount());
        return getBankCardDAO().findByGameAccount(gameAccount, PaginationUtils.convert(request));
    }

    @Transactional
    public BankCard getBankCard(CardRequest request, TokenData token, TokenAuthorityEnum authority) {
        final Optional<BankCard> optCard = getBankCardDAO().findByNumber(request.getCardNumber());
        if (optCard.isEmpty()) {
            throw CardException.UNKNOWN_CARD;
        }

        final BankCard card = optCard.get();
        if (authority == null || !getTokenService().hasAuthority(token.token(), authority)) {
            getGameAccountService().validateOwnedWithResult(token, card.getGameAccount());
        }

        return card;
    }

    public TokenService getTokenService() {
        return tokenService;
    }

    public GameAccountService getGameAccountService() {
        return gameAccountService;
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
