package me.statuxia.shulkerapi.controller.api.card;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.api.AuthController;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.CardRequest;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Tag(name = "Card", description = "Контроллер для работы с картами")
public abstract class CardController implements AuthController {

    public static final String PREFIX = "/api/v1/card";

    private final TokenService tokenService;
    private final GameAccountService gameAccountService;
    private final BankCardDAO bankCardDAO;
    private final CardProperties cardProperties;
    private final OperationProcessorService operationProcessorService;
    private final CardHistoryService cardHistoryService;

    @Autowired
    protected CardController(
        TokenService tokenService,
        GameAccountService gameAccountService,
        BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService,
        CardHistoryService cardHistoryService
    ) {
        this.tokenService = tokenService;
        this.gameAccountService = gameAccountService;
        this.bankCardDAO = bankCardDAO;
        this.cardProperties = cardProperties;
        this.operationProcessorService = operationProcessorService;
        this.cardHistoryService = cardHistoryService;
    }

    @Transactional
    public BankCard getBankCard(CardRequest request, TokenData token, TokenAuthorityEnum authority) {
        final Optional<BankCard> optCard = getBankCardDAO().findByNumber(request.getCardNumber());
        if (optCard.isEmpty()) {
            throw CardException.UNKNOWN_CARD;
        }

        final BankCard card = optCard.get();
        if (authority == null || !getTokenService().hasAuthority(token.token(), authority)) {
            if (!getGameAccountService().validateOwnedWithResult(token, card.getGameAccount())) {
                throw CardException.UNKNOWN_CARD;
            }
        }

        if (!getGameAccountService().getGameAccount(request.getGameAccount()).equals(card.getGameAccount())) {
            throw CardException.UNKNOWN_CARD;
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

    public CardHistoryService getCardHistoryService() {
        return cardHistoryService;
    }
}
