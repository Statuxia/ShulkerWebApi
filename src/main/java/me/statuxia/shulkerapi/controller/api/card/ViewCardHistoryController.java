package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.converter.JsonNodeConverter;
import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.dao.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dao.BankCardOperationHistoryDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistory;
import me.statuxia.shulkerapi.model.BankCardOperationHistory;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.CardHistoryRequest;
import me.statuxia.shulkerapi.response.BankCardHistoryItem;
import me.statuxia.shulkerapi.response.BankCardHistoryPaginationResponse;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.service.impl.MessageService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.ViewCardHistoryControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.card.CardDisabledOperation;
import me.statuxia.shulkerapi.swagger.controller.card.UnknownCardOperation;
import me.statuxia.shulkerapi.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class ViewCardHistoryController extends CardController {

    public static final String LIST = "/history/list";

    private final BankCardHistoryDAO bankCardHistoryDAO;
    private final BankCardOperationHistoryDAO bankCardOperationHistoryDAO;
    private final MessageService messageService;
    private final JsonNodeConverter jsonNodeConverter;
    private final ViewCardHistoryController controller;

    @Autowired
    public ViewCardHistoryController(
        TokenService tokenService,
        GameAccountService gameAccountService,
        BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService,
        CardHistoryService cardHistoryService, BankCardHistoryDAO bankCardHistoryDAO,
        BankCardOperationHistoryDAO bankCardOperationHistoryDAO, MessageService messageService,
        JsonNodeConverter jsonNodeConverter
    ) {
        super(
            tokenService,
            gameAccountService,
            bankCardDAO,
            cardProperties,
            operationProcessorService,
            cardHistoryService
        );
        this.bankCardHistoryDAO = bankCardHistoryDAO;
        this.bankCardOperationHistoryDAO = bankCardOperationHistoryDAO;
        this.messageService = messageService;
        this.jsonNodeConverter = jsonNodeConverter;
        this.controller = this;
    }

    @PostMapping(value = LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownCardOperation
    @CardDisabledOperation
    @ViewCardHistoryControllerOperation.List
    public ResponseEntity<BankCardHistoryPaginationResponse> list(
        @AuthData TokenData token,
        @RequestBody @Valid CardHistoryRequest request
    ) {
        final BankCard card = getController().getBankCard(request, token, TokenAuthorityEnum.LIST_BANK_CARD_HISTORY);
        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        final BankCardHistoryPaginationResponse response = new BankCardHistoryPaginationResponse();
        if (CollectionUtils.isEmpty(request.getTypes())) {
            response.setTotal(bankCardHistoryDAO.countByCard(card));
            response.setItems(
                bankCardHistoryDAO.findByCard(card).stream()
                    .map(this::map).toList()
            );
            return ResponseEntity.ok(response);
        } else {
            response.setTotal(bankCardHistoryDAO.countByCardAndTypeIn(card, request.getTypes()));
            response.setItems(
                bankCardHistoryDAO.findByCardAndTypeIn(card, request.getTypes()).stream()
                    .map(this::map).toList()
            );
            return ResponseEntity.ok(response);
        }
    }

    protected BankCardHistoryItem map(BankCardHistory history) {
        final List<BankCardOperationHistory> list = bankCardOperationHistoryDAO.findByUuid(history.getUuid());
        final BankCardHistoryItem item = new BankCardHistoryItem();
        item.setId(history.getId());
        item.setType(messageService.message(history.getType()));
        item.setDateTime(history.getCreateTime().toString(DateUtils.DATETIME));
        item.setData(jsonNodeConverter.convertToDatabaseColumn(history.getHistoryData()));

        if (!list.isEmpty()) {
            item.setState(list.getFirst().getState());
        }

        return item;
    }

    public ViewCardHistoryController getController() {
        return controller;
    }
}
