package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardMemberDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardMemberSettingDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardSettingDAO;
import me.statuxia.shulkerapi.dto.CardHistoryAdditionalData;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.dto.search.impl.BankCardMemberSearchDTO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardMemberSettingSearchDTO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardSettingSearchDTO;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.processor.impl.card.BalanceProcessor;
import me.statuxia.shulkerapi.processor.impl.card.TransferFundsProcessor;
import me.statuxia.shulkerapi.request.ChangeCardBalanceRequest;
import me.statuxia.shulkerapi.request.TransferFundsRequest;
import me.statuxia.shulkerapi.service.BankCardService;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.PinAdapter;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.service.impl.MessageService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.CardActionControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.card.*;
import me.statuxia.shulkerapi.swagger.controller.funds.AmountGreaterZeroOperation;
import me.statuxia.shulkerapi.swagger.controller.funds.NotEnoughFundsOperation;
import me.statuxia.shulkerapi.utils.AdminCardHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static me.statuxia.shulkerapi.model.TokenAuthorityEnum.*;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class CardActionController extends CardController {

    public static final String DEPOSIT = "/deposit";
    public static final String WITHDRAW = "/withdraw";
    public static final String TRANSFER = "/transfer";

    private static final String PERMISSION_OWNER = "OWNER";
    private static final String PERMISSION_MEMBERS = "MEMBERS";

    private final CardActionController controller;
    private final BankCardService bankCardService;
    private final BankCardMemberDAO bankCardMemberDAO;
    private final BankCardSettingDAO bankCardSettingDAO;
    private final BankCardMemberSettingDAO bankCardMemberSettingDAO;

    @Autowired
    public CardActionController(
        TokenService tokenService,
        GameAccountService gameAccountService, BankCardDAO bankCardDAO,
        CardProperties cardProperties,
        OperationProcessorService operationProcessorService,
        CardHistoryService cardHistoryService, MessageService messageService,
        BankCardService bankCardService,
        BankCardMemberDAO bankCardMemberDAO,
        BankCardSettingDAO bankCardSettingDAO,
        BankCardMemberSettingDAO bankCardMemberSettingDAO,
        PinAdapter pinAdapter
    ) {
        super(
            tokenService, gameAccountService, bankCardDAO, cardProperties,
            operationProcessorService, cardHistoryService, messageService, pinAdapter
        );
        this.controller = this;
        this.bankCardService = bankCardService;
        this.bankCardMemberDAO = bankCardMemberDAO;
        this.bankCardSettingDAO = bankCardSettingDAO;
        this.bankCardMemberSettingDAO = bankCardMemberSettingDAO;
    }

    @RequiredAuthority(requireAll = DEPOSIT_FUNDS_TO_CARD)
    @PostMapping(value = DEPOSIT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CardActionControllerOperation.Deposit
    @CardDisabledOperation
    @UnknownCardOperation
    @UnknownAccountOperation
    @AmountGreaterZeroOperation
    @UnsupportedForAdminCardOperation
    @UnknownMemberOperation
    @OperationNotPermittedOperation
    public ResponseEntity<Void> deposit(
        @RequestBody @Valid ChangeCardBalanceRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, DEPOSIT_FUNDS_TO_CARD);
        AdminCardHelper.unsupportedForAdminCard(card);

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        if (CardType.GROUP.equals(card.getType())) {
            processGroupDeposit(card, request);
        } else {
            final OperationData data = new OperationData()
                .addProcessor(BalanceProcessor.class)
                .addData(BalanceProcessor.CARD, card)
                .addData(BalanceProcessor.OPERATION, CardOperationType.DEPOSIT)
                .addData(BalanceProcessor.VALUE, request.getFunds());
            getOperationProcessorService().process(data);
        }

        return ResponseEntity.ok().build();
    }

    @RequiredAuthority(requireAll = WITHDRAW_FUNDS_FROM_CARD)
    @PostMapping(value = WITHDRAW, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CardDisabledOperation
    @UnknownCardOperation
    @InvalidPinOperation
    @CardActionControllerOperation.Withdraw
    @UnknownAccountOperation
    @AmountGreaterZeroOperation
    @UnknownMemberOperation
    @OperationNotPermittedOperation
    public ResponseEntity<Void> withdraw(
        @RequestBody @Valid ChangeCardBalanceRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, WITHDRAW_FUNDS_FROM_CARD);
        if (CardType.ADMIN.equals(card.getType())
            && !getTokenService().hasAuthority(token.token(), ADMIN_CARD_WITHDRAW)
        ) {
            throw CardException.UNKNOWN_CARD;
        }

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        if (CardType.GROUP.equals(card.getType())) {
            processGroupWithdraw(card, request);
        } else {
            if (card.getPin() == null || !getPinAdapter().pinMatches(request.getPin(), card.getPin())) {
                throw CardException.INVALID_PIN;
            }

            final OperationData data = new OperationData()
                .addProcessor(BalanceProcessor.class)
                .addData(BalanceProcessor.CARD, card)
                .addData(BalanceProcessor.OPERATION, CardOperationType.WITHDRAW)
                .addData(BalanceProcessor.VALUE, request.getFunds());
            getOperationProcessorService().process(data);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = TRANSFER, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CardDisabledOperation
    @ReceiverCardDisabledOperation
    @UnknownCardOperation
    @InvalidPinOperation
    @CardActionControllerOperation.Transfer
    @UnknownAccountOperation
    @AmountGreaterZeroOperation
    @NotEnoughFundsOperation
    @UnsupportedForAdminCardOperation
    @SameCardReceiverOperation
    @GroupCardSenderOperation
    @OperationNotPermittedOperation
    public ResponseEntity<Void> transfer(
        @RequestBody @Valid TransferFundsRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = getController().getBankCard(request, token, TRANSFER_FUNDS_FROM_CARD);
        AdminCardHelper.unsupportedForAdminCard(card);

        if (CardType.GROUP.equals(card.getType())) {
            throw CardException.GROUP_CARD_SENDER;
        }

        final BankCard receiverCard = getBankCardDAO().findByNumber(request.getReceiverCard())
            .orElseThrow(() -> CardException.UNKNOWN_RECEIVER_CARD);

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        if (receiverCard.isDisabled()) {
            throw CardException.RECEIVER_CARD_DISABLED;
        }

        if (card.getPin() == null || !getPinAdapter().pinMatches(request.getPin(), card.getPin())) {
            throw CardException.INVALID_PIN;
        }

        if (CardType.GROUP.equals(receiverCard.getType())) {
            checkTransferPermission(card, receiverCard);
        }

        final OperationData data = new OperationData()
            .addProcessor(TransferFundsProcessor.class)
            .addData(TransferFundsProcessor.MESSAGE, request.getMessage() == null ? "" : request.getMessage())
            .addData(TransferFundsProcessor.CARD, card)
            .addData(TransferFundsProcessor.RECEIVER, receiverCard)
            .addData(TransferFundsProcessor.VALUE, request.getFunds());
        getOperationProcessorService().process(data);

        return ResponseEntity.ok().build();
    }

    private void processGroupDeposit(BankCard card, ChangeCardBalanceRequest request) {
        if (request.getMemberGameAccount() != null) {
            final GameAccount memberGA = getGameAccountService().getGameAccount(request.getMemberGameAccount());
            final BankCardMember member = findMember(card, memberGA);

            checkOperationPermission(card, member, BankCardSettingType.DEPOSIT_PERMISSION);

            final List<CardHistoryAdditionalData> additionalData = List.of(
                new CardHistoryAdditionalData("game_account_id", String.valueOf(memberGA.getId()))
            );
            bankCardService.depositFunds(card, request.getFunds(), additionalData);

            member.setCredited(member.getCredited() + request.getFunds());
            bankCardMemberDAO.save(member);
        } else {
            bankCardService.depositFunds(card, request.getFunds());
        }
    }

    private void processGroupWithdraw(BankCard card, ChangeCardBalanceRequest request) {
        if (request.getMemberGameAccount() != null) {
            final GameAccount memberGA = getGameAccountService().getGameAccount(request.getMemberGameAccount());
            final BankCardMember member = findMember(card, memberGA);

            if (!getPinAdapter().pinMatches(request.getPin(), member.getPin())) {
                throw CardException.INVALID_PIN;
            }

            checkOperationPermission(card, member, BankCardSettingType.WITHDRAW_PERMISSION);

            final List<CardHistoryAdditionalData> additionalData = List.of(
                new CardHistoryAdditionalData("game_account_id", String.valueOf(memberGA.getId()))
            );
            bankCardService.withdrawFunds(card, request.getFunds(), false, additionalData);

            member.setDebited(member.getDebited() + request.getFunds());
            bankCardMemberDAO.save(member);
        } else {
            if (card.getPin() == null || !getPinAdapter().pinMatches(request.getPin(), card.getPin())) {
                throw CardException.INVALID_PIN;
            }
            bankCardService.withdrawFunds(card, request.getFunds(), false);
        }
    }

    private BankCardMember findMember(BankCard card, GameAccount gameAccount) {
        return bankCardMemberDAO.find(
            new BankCardMemberSearchDTO().setCard(card).setGameAccount(gameAccount)
        ).orElseThrow(() -> CardException.UNKNOWN_MEMBER);
    }

    private void checkOperationPermission(BankCard card, BankCardMember member, BankCardSettingType settingType) {
        final Optional<BankCardSetting> cardSetting = bankCardSettingDAO.find(
            new BankCardSettingSearchDTO().setCard(card).setType(settingType)
        );

        if (cardSetting.isEmpty() || PERMISSION_MEMBERS.equals(cardSetting.get().getValue())) {
            return;
        }

        if (PERMISSION_OWNER.equals(cardSetting.get().getValue())) {
            final Optional<BankCardMemberSetting> memberOverride = bankCardMemberSettingDAO.find(
                new BankCardMemberSettingSearchDTO().setBankCardMember(member).setType(settingType)
            );
            if (memberOverride.isEmpty() || !PERMISSION_MEMBERS.equals(memberOverride.get().getValue())) {
                throw CardException.OPERATION_NOT_PERMITTED;
            }
        }
    }

    private void checkTransferPermission(BankCard senderCard, BankCard receiverGroupCard) {
        final Optional<BankCardSetting> transferSetting = bankCardSettingDAO.find(
            new BankCardSettingSearchDTO().setCard(receiverGroupCard).setType(BankCardSettingType.TRANSFER_PERMISSION)
        );

        if (transferSetting.isEmpty()) {
            return;
        }

        final String permission = transferSetting.get().getValue();
        final GameAccount senderGameAccount = senderCard.getGameAccount();

        if (PERMISSION_OWNER.equals(permission)) {
            if (!senderGameAccount.equals(receiverGroupCard.getGameAccount())) {
                throw CardException.OPERATION_NOT_PERMITTED;
            }
        } else if (PERMISSION_MEMBERS.equals(permission)) {
            final boolean isOwner = senderGameAccount.equals(receiverGroupCard.getGameAccount());
            if (!isOwner) {
                final Optional<BankCardMember> senderAsMember = bankCardMemberDAO.find(
                    new BankCardMemberSearchDTO().setCard(receiverGroupCard).setGameAccount(senderGameAccount)
                );
                if (senderAsMember.isEmpty()) {
                    throw CardException.OPERATION_NOT_PERMITTED;
                }
            }
        }
    }

    public CardActionController getController() {
        return controller;
    }
}
