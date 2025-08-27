package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.request.CardRequest;
import me.statuxia.shulkerapi.response.BankCardItem;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class ViewCardController extends CardController {

    public static final String GET = "/get";
    public static final String LIST = "/list";

    protected ViewCardController controller;

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
    @Transactional
    public ResponseEntity<List<BankCardItem>> list(
        @AuthData TokenData token,
        @RequestBody @Valid CardRequest request
    ) {
        return ResponseEntity.ok(getController().getBankCards(request, token).stream().map(this::buildItem).toList());
    }

    @PostMapping(value = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<BankCardItem> get(
        @AuthData TokenData token,
        @RequestBody @Valid CardRequest request
    ) {
        final Optional<BankCard> optCard = getBankCardDAO().findByNumber(request.getCardNumber());
        if (optCard.isEmpty()) {
            throw CardException.UNKNOWN_CARD;
        }

        final BankCard card = optCard.get();

        final BankCardItem item = buildItem(card);
        if (
            !getGameAccountService().validateOwnedWithResult(token, card.getGameAccount())
            || !getGameAccountService().getGameAccount(request.getGameAccount()).equals(card.getGameAccount())
        ) {
            item.setCurrency(null);
            item.setDisabled(null);
        }
        return ResponseEntity.ok(item);
    }

    private BankCardItem buildItem(BankCard card) {
        return new BankCardItem()
            .setId(card.getId())
            .setCardNumber(card.getNumber())
            .setGameAccount(card.getGameAccount() == null ? null : card.getGameAccount().getName())
            .setCurrency(card.getCurrency())
            .setDisabled(card.isDisabled());
    }

    public ViewCardController getController() {
        return controller;
    }

    @Autowired
    @Lazy
    public void setController(ViewCardController controller) {
        this.controller = controller;
    }
}
