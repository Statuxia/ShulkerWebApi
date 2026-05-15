package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.CardHistoryException;
import me.statuxia.shulkerapi.model.BankCardHistory;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.request.CardOperationRequest;
import me.statuxia.shulkerapi.service.*;
import me.statuxia.shulkerapi.service.impl.MessageService;
import me.statuxia.shulkerapi.swagger.UnknownActionByAccountOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static me.statuxia.shulkerapi.model.TokenAuthorityEnum.HISTORY_RESTORE_OPERATION;
import static me.statuxia.shulkerapi.model.TokenAuthorityEnum.HISTORY_ROLLBACK_OPERATION;

@RestController
@RequestMapping(value = CardOperationController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class CardOperationController extends CardController {

    public static final String PREFIX = CardController.PREFIX + "/operation";
    public static final String ROLLBACK = "/rollback";
    public static final String RESTORE = "/restore";
    private static final Set<BankCardHistoryType> HISTORY_TYPES = Set.of(
        BankCardHistoryType.DEPOSIT,
        BankCardHistoryType.WITHDRAW,
        BankCardHistoryType.TRANSFER_FROM,
        BankCardHistoryType.TRANSFER_TO,
        BankCardHistoryType.PAY_FINE
    );

    private final BankCardService bankCardService;
    private final BankCardHistoryDAO bankCardHistoryDAO;

    @Autowired
    public CardOperationController(
        TokenService tokenService,
        GameAccountService gameAccountService,
        BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService,
        CardHistoryService cardHistoryService, BankCardService bankCardService,
        BankCardHistoryDAO bankCardHistoryDAO, MessageService messageService,
        BCryptPasswordEncoder passwordEncoder
    ) {
        super(
            tokenService,
            gameAccountService,
            bankCardDAO,
            cardProperties,
            operationProcessorService,
            cardHistoryService,
            messageService,
            passwordEncoder
        );
        this.bankCardService = bankCardService;
        this.bankCardHistoryDAO = bankCardHistoryDAO;
    }

    @RequiredAuthority(requireAll = HISTORY_ROLLBACK_OPERATION)
    @PostMapping(value = ROLLBACK + "/{historyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownActionByAccountOperation
    public ResponseEntity<Void> rollback(
        @AuthData TokenData token, @PathVariable("historyId") Long historyId,
        @RequestBody @Valid CardOperationRequest request
    ) {
        final BankCardHistory history = bankCardHistoryDAO.findById(historyId)
            .orElseThrow(() -> CardHistoryException.UNKNOWN_HISTORY);
        final GameAccount actionBy = getGameAccountService().getActionGameAccount(request.getActionBy());

        if (!HISTORY_TYPES.contains(history.getType())) {
            throw CardHistoryException.WRONG_HISTORY_TYPE;
        }

        bankCardService.rollbackFunds(history.getUuid(), actionBy);

        return ResponseEntity.ok().build();
    }

    @RequiredAuthority(requireAll = HISTORY_RESTORE_OPERATION)
    @PostMapping(value = RESTORE + "/{historyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownActionByAccountOperation
    public ResponseEntity<Void> restore(
        @AuthData TokenData token, @PathVariable("historyId") Long historyId,
        @RequestBody @Valid CardOperationRequest request
    ) {
        final BankCardHistory history = bankCardHistoryDAO.findById(historyId)
            .orElseThrow(() -> CardHistoryException.UNKNOWN_HISTORY);
        final GameAccount actionBy = getGameAccountService().getActionGameAccount(request.getActionBy());

        if (!HISTORY_TYPES.contains(history.getType())) {
            throw CardHistoryException.WRONG_HISTORY_TYPE;
        }

        bankCardService.restoreFunds(history.getUuid(), actionBy);

        return ResponseEntity.ok().build();
    }
}
