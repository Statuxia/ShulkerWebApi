package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.search.impl.BankCardSearchDTO;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.CardRequest;
import me.statuxia.shulkerapi.response.BankCardItem;
import me.statuxia.shulkerapi.response.BankCardPaginationResponse;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.ViewCardControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.card.UnknownCardOperation;
import me.statuxia.shulkerapi.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class ViewCardController extends CardController {

    public static final String GET = "/get";
    public static final String LIST = "/list";

    @Autowired
    public ViewCardController(
        TokenService tokenService,
        GameAccountService gameAccountService,
        BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService,
        CardHistoryService cardHistoryService
    ) {
        super(
            tokenService, gameAccountService, bankCardDAO, cardProperties,
            operationProcessorService, cardHistoryService
        );
    }

    @PostMapping(value = LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownCardOperation
    @ViewCardControllerOperation.List
    public ResponseEntity<BankCardPaginationResponse> list(
        @AuthData TokenData token,
        @RequestBody @Valid CardRequest request
    ) {

        return ResponseEntity.ok(getBankCards(request, token));
    }

    @PostMapping(value = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownCardOperation
    @UnknownAccountOperation
    @ViewCardControllerOperation.Get
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
            item.setCreateTime(null);
        }
        return ResponseEntity.ok(item);
    }

    private BankCardPaginationResponse getBankCards(CardRequest request, TokenData token) {
        final GameAccount gameAccount = getGameAccountService().getGameAccount(
            token,
            request.getGameAccount(),
            TokenAuthorityEnum.LIST_BANK_CARD
        );

        final BankCardPaginationResponse response = new BankCardPaginationResponse();
        final BankCardSearchDTO dto = new BankCardSearchDTO()
            .setPageable(request.getPageable())
            .setGameAccount(gameAccount)
            .setCardType(request.getCardType())
            .setStartCreateTime(request.getCreateFrom())
            .setEndCreateTime(request.getCreateTo());
        response.setTotal(getBankCardDAO().count(dto));
        response.setItems(getBankCardDAO().findList(dto).stream().map(this::buildItem).toList());

        return response;
    }

    private BankCardItem buildItem(BankCard card) {
        return new BankCardItem()
            .setId(card.getId())
            .setCardNumber(card.getNumber())
            .setGameAccount(card.getGameAccount() == null ? null : card.getGameAccount().getName())
            .setCurrency(card.getCurrency())
            .setCreateTime(card.getCreateTime().toString(DateUtils.DATETIME))
            .setDisabled(card.isDisabled());
    }
}
