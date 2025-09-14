package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.converter.JsonNodeConverter;
import me.statuxia.shulkerapi.dao.BankCardLogDAO;
import me.statuxia.shulkerapi.dao.BankCardOperationHistoryDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.search.impl.BankCardHistorySearchDTO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardSearchDTO;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.request.CardHistoryRequest;
import me.statuxia.shulkerapi.response.BankCardHistoryItem;
import me.statuxia.shulkerapi.response.BankCardHistoryLogItem;
import me.statuxia.shulkerapi.response.BankCardHistoryPaginationResponse;
import me.statuxia.shulkerapi.response.NamedItem;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.service.impl.MessageService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.ViewCardControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.ViewCardHistoryControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.card.CardDisabledOperation;
import me.statuxia.shulkerapi.swagger.controller.card.UnknownCardOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class ViewCardHistoryController extends CardController {

    public static final String LIST = "/history/list";
    public static final String TYPES = "/history/types";

    private final BankCardHistoryDAO bankCardHistoryDAO;
    private final BankCardOperationHistoryDAO bankCardOperationHistoryDAO;
    private final BankCardLogDAO bankCardLogDAO;
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
        BankCardOperationHistoryDAO bankCardOperationHistoryDAO, BankCardLogDAO bankCardLogDAO,
        MessageService messageService,
        JsonNodeConverter jsonNodeConverter
    ) {
        super(
            tokenService,
            gameAccountService,
            bankCardDAO,
            cardProperties,
            operationProcessorService,
            cardHistoryService,
            messageService
        );
        this.bankCardHistoryDAO = bankCardHistoryDAO;
        this.bankCardOperationHistoryDAO = bankCardOperationHistoryDAO;
        this.bankCardLogDAO = bankCardLogDAO;
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
        final List<BankCard> cards = new ArrayList<>();
        if (StringUtils.hasText(request.getCardNumber())) {
            final BankCard card = getController().getBankCard(
                request, token,
                TokenAuthorityEnum.LIST_BANK_CARD_HISTORY
            );
            cards.add(card);
        } else {
            final BankCardSearchDTO bankCardSearchDTO = new BankCardSearchDTO();
            final GameAccount gameAccount = getGameAccountService().getGameAccount(request.getGameAccount());
            bankCardSearchDTO.setGameAccount(gameAccount);
            cards.addAll(getController().getBankCardDAO().findList(bankCardSearchDTO));
        }

        final BankCardHistorySearchDTO dto = new BankCardHistorySearchDTO()
            .setCards(cards)
            .setStartCreateTime(request.getCreateFrom())
            .setEndCreateTime(request.getCreateTo())
            .setTypes(request.getHistoryTypes())
            .setPageable(request.getPageable());

        final BankCardHistoryPaginationResponse response = new BankCardHistoryPaginationResponse();
        response.setTotal(bankCardHistoryDAO.count(dto));
        response.setItems(bankCardHistoryDAO.findList(dto).stream().map(this::map).toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = TYPES, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ViewCardControllerOperation.Types
    public ResponseEntity<List<NamedItem>> types() {
        return ResponseEntity.ok(Arrays.stream(BankCardHistoryType.values()).map(type -> new NamedItem(
            getMessageService().message(type), type.name()
        )).toList());
    }

    protected BankCardHistoryItem map(BankCardHistory history) {
        final List<BankCardOperationHistory> list = bankCardOperationHistoryDAO.findByUuidAndCard(
            history.getUuid(), history.getCard()
        );
        final List<BankCardLog> logs = bankCardLogDAO.findByUuidAndCard(history.getUuid(), history.getCard());
        final BankCardHistoryItem item = new BankCardHistoryItem();
        item.setId(history.getId());
        item.setType(getMessageService().message(history.getType()));
        item.setDateTime(history.getCreateTime().getMillis());
        item.setData(jsonNodeConverter.convertToI18nMap(history.getHistoryData()));
        item.setLogs(logs.stream().map(this::map).toList());

        if (!list.isEmpty()) {
            item.setState(list.getFirst().getState());
        }

        return item;
    }

    protected BankCardHistoryLogItem map(BankCardLog log) {
        final BankCardHistoryLogItem logItem = new BankCardHistoryLogItem();
        logItem.setUsername(log.getActionBy());
        logItem.setDateTime(log.getActionTime().getMillis());
        logItem.setData(jsonNodeConverter.convertToI18nMap(log.getData()));
        return logItem;
    }

    public ViewCardHistoryController getController() {
        return controller;
    }
}
