package me.statuxia.shulkerapi.controller.api.fine;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.controller.api.Controller;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.converter.JsonNodeConverter;
import me.statuxia.shulkerapi.dao.impl.FineDAO;
import me.statuxia.shulkerapi.dao.impl.FineLogDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.search.impl.FineLogSearchDTO;
import me.statuxia.shulkerapi.dto.search.impl.FineSearchDTO;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.request.FineListRequest;
import me.statuxia.shulkerapi.response.FineItem;
import me.statuxia.shulkerapi.response.FineListResponse;
import me.statuxia.shulkerapi.response.FineLogItem;
import me.statuxia.shulkerapi.response.NamedItem;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.impl.MessageService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.UnknownActionByAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.ViewFineControllerOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = ViewFineController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class ViewFineController implements Controller {

    public static final String PREFIX = "/api/v1/fine";
    public static final String LIST = "/list";
    public static final String ACTION_TYPES = "/action/types";
    public static final String STATUS_TYPES = "/status/types";

    private final FineDAO fineDAO;
    private final FineLogDAO fineLogDAO;
    private final MessageService messageService;
    private final GameAccountService gameAccountService;
    private final JsonNodeConverter jsonNodeConverter;

    @Autowired
    public ViewFineController(
        FineDAO fineDAO,
        FineLogDAO fineLogDAO,
        MessageService messageService,
        GameAccountService gameAccountService,
        JsonNodeConverter jsonNodeConverter
    ) {
        this.fineDAO = fineDAO;
        this.fineLogDAO = fineLogDAO;
        this.messageService = messageService;
        this.gameAccountService = gameAccountService;
        this.jsonNodeConverter = jsonNodeConverter;
    }

    @PostMapping(value = LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownActionByAccountOperation
    @ViewFineControllerOperation.List
    public ResponseEntity<FineListResponse> list(
        @AuthData TokenData token,
        @RequestBody @Valid FineListRequest request
    ) {
        final List<Fine> fines = fineDAO.findList(new FineSearchDTO()
            .setPageable(request.getPageable())
            .setActionBy(request.getActionBy())
            .setGameAccount(gameAccountService.getGameAccount(
                token, request.getGameAccount(),
                TokenAuthorityEnum.LIST_FINE)
            )
            .setUnnotifiedOnly(request.isUnnotifiedOnly())
            .setStatuses(request.getStatuses()));
        final Map<Long, List<FineLog>> fineIdLogs = fineLogDAO.findList(new FineLogSearchDTO()
            .setFineList(fines)).stream().collect(Collectors.groupingBy(k -> k.getFine().getId()));
        final List<FineItem> fineItems = fines.stream().map(fine -> {
            final List<FineLogItem> logs = fineIdLogs.getOrDefault(fine.getId(), List.of()).stream()
                .map(log -> new FineLogItem()
                .setLogDate(log.getLogDate())
                .setData(jsonNodeConverter.convertToI18nMap(log.getData()))
                .setAction(messageService.message(log.getAction()))
                .setActionBy(log.getActionBy())).toList();
            return new FineItem(fine).setLogs(logs);
        }).toList();
        final FineListResponse response = new FineListResponse();
        response.setItems(fineItems);
        response.setTotal(fineItems.size());

        fines.stream()
            .filter(fine -> !fine.isNotified())
            .forEach(fine -> {
                fine.setNotified(true);
                fineDAO.save(fine);
            });

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = ACTION_TYPES, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ViewFineControllerOperation.ActionTypes
    public ResponseEntity<List<NamedItem>> actionTypes() {
        return ResponseEntity.ok(Arrays.stream(FineAction.values()).map(type -> new NamedItem(
            messageService.message(type), type.name()
        )).toList());
    }

    @GetMapping(value = STATUS_TYPES, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ViewFineControllerOperation.StatusTypes
    public ResponseEntity<List<NamedItem>> statusTypes() {
        return ResponseEntity.ok(Arrays.stream(FineStatus.values()).map(type -> new NamedItem(
            messageService.message(type), type.name()
        )).toList());
    }
}
