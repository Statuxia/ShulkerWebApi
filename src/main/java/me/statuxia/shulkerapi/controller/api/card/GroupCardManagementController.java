package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardMemberDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardMemberSettingDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardSettingDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.search.impl.BankCardMemberSearchDTO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardMemberSettingSearchDTO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardSettingSearchDTO;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.request.*;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.PinAdapter;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.service.impl.MessageService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.GroupCardManagementControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.card.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static me.statuxia.shulkerapi.model.TokenAuthorityEnum.*;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class GroupCardManagementController extends CardController {

    public static final String GROUP_SETTING_UPDATE = "/group/setting/update";
    public static final String GROUP_MEMBER_ADD = "/group/member/add";
    public static final String GROUP_MEMBER_REMOVE = "/group/member/remove";
    public static final String GROUP_MEMBER_UPDATE_PIN = "/group/member/update-pin";
    public static final String GROUP_MEMBER_SETTING_UPDATE = "/group/member/setting/update";

    private final BankCardSettingDAO bankCardSettingDAO;
    private final BankCardMemberDAO bankCardMemberDAO;
    private final BankCardMemberSettingDAO bankCardMemberSettingDAO;
    private final GroupCardManagementController controller;

    @Autowired
    public GroupCardManagementController(
        TokenService tokenService, GameAccountService gameAccountService, BankCardDAO bankCardDAO,
        CardProperties cardProperties, OperationProcessorService operationProcessorService,
        CardHistoryService cardHistoryService, MessageService messageService,
        BankCardSettingDAO bankCardSettingDAO, BankCardMemberDAO bankCardMemberDAO,
        BankCardMemberSettingDAO bankCardMemberSettingDAO, PinAdapter pinAdapter
    ) {
        super(
            tokenService, gameAccountService, bankCardDAO, cardProperties,
            operationProcessorService, cardHistoryService, messageService, pinAdapter
        );
        this.bankCardSettingDAO = bankCardSettingDAO;
        this.bankCardMemberDAO = bankCardMemberDAO;
        this.bankCardMemberSettingDAO = bankCardMemberSettingDAO;
        this.controller = this;
    }

    @PutMapping(value = GROUP_SETTING_UPDATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownCardOperation
    @NotGroupCardOperation
    @CardDisabledOperation
    @InvalidPinOperation
    @GroupCardManagementControllerOperation.UpdateSetting
    public ResponseEntity<Void> updateSetting(
        @RequestBody @Valid GroupCardSettingUpdateRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = controller.getBankCard(request, token, UPDATE_GROUP_CARD_SETTING);

        if (!CardType.GROUP.equals(card.getType())) {
            throw CardException.NOT_GROUP_CARD;
        }

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        final boolean isAdmin = getTokenService().hasAuthority(token.token(), UPDATE_GROUP_CARD_SETTING);
        if (!isAdmin && !getPinAdapter().pinMatches(request.getPin(), card.getPin())) {
            throw CardException.INVALID_PIN;
        }

        final Optional<BankCardSetting> existing = bankCardSettingDAO.find(
            new BankCardSettingSearchDTO().setCard(card).setType(request.getType())
        );

        if (existing.isPresent()) {
            existing.get().setValue(request.getValue());
            bankCardSettingDAO.save(existing.get());
        } else {
            final BankCardSetting setting = new BankCardSetting();
            setting.setCard(card);
            setting.setType(request.getType());
            setting.setValue(request.getValue());
            bankCardSettingDAO.save(setting);
        }

        getCardHistoryService().writeUpdateGroupCardSetting(card);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = GROUP_MEMBER_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownCardOperation
    @NotGroupCardOperation
    @CardDisabledOperation
    @InvalidPinOperation
    @MemberIsCardOwnerOperation
    @GroupCardManagementControllerOperation.AddMember
    public ResponseEntity<Void> addMember(
        @RequestBody @Valid GroupCardMemberAddRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = controller.getBankCard(request, token, MANAGE_GROUP_CARD_MEMBERS);

        if (!CardType.GROUP.equals(card.getType())) {
            throw CardException.NOT_GROUP_CARD;
        }

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        final boolean isAdmin = getTokenService().hasAuthority(token.token(), MANAGE_GROUP_CARD_MEMBERS);
        if (!isAdmin && !getPinAdapter().pinMatches(request.getCardPin(), card.getPin())) {
            throw CardException.INVALID_PIN;
        }

        if (card.getGameAccount().getName().equals(request.getMemberGameAccount())) {
            throw CardException.MEMBER_IS_CARD_OWNER;
        }

        final GameAccount memberGameAccount = getGameAccountService().getGameAccount(request.getMemberGameAccount());

        final BankCardMember member = new BankCardMember();
        member.setCard(card);
        member.setGameAccount(memberGameAccount);
        member.setPin(getPinAdapter().encodePin(request.getPin()));
        member.setAddedAt(DateTime.now());
        member.setCredited(0L);
        member.setDebited(0L);

        bankCardMemberDAO.save(member);
        getCardHistoryService().writeAddGroupCardMember(card);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = GROUP_MEMBER_REMOVE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownCardOperation
    @NotGroupCardOperation
    @UnknownMemberOperation
    @CardDisabledOperation
    @InvalidPinOperation
    @GroupCardManagementControllerOperation.RemoveMember
    public ResponseEntity<Void> removeMember(
        @RequestBody @Valid GroupCardMemberRemoveRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = controller.getBankCard(request, token, MANAGE_GROUP_CARD_MEMBERS);

        if (!CardType.GROUP.equals(card.getType())) {
            throw CardException.NOT_GROUP_CARD;
        }

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        final boolean isAdmin = getTokenService().hasAuthority(token.token(), MANAGE_GROUP_CARD_MEMBERS);
        if (!isAdmin && !getPinAdapter().pinMatches(request.getPin(), card.getPin())) {
            throw CardException.INVALID_PIN;
        }

        final GameAccount memberGameAccount = getGameAccountService().getGameAccount(request.getMemberGameAccount());

        final Optional<BankCardMember> member = bankCardMemberDAO.find(
            new BankCardMemberSearchDTO().setCard(card).setGameAccount(memberGameAccount)
        );
        if (member.isEmpty()) {
            throw CardException.UNKNOWN_MEMBER;
        }

        bankCardMemberSettingDAO.findList(
            new BankCardMemberSettingSearchDTO().setBankCardMember(member.get())
        ).forEach(bankCardMemberSettingDAO::forceDelete);
        bankCardMemberDAO.forceDelete(member.get());
        getCardHistoryService().writeRemoveGroupCardMember(card);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = GROUP_MEMBER_UPDATE_PIN, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownCardOperation
    @NotGroupCardOperation
    @UnknownMemberOperation
    @CardDisabledOperation
    @InvalidPinOperation
    @GroupCardManagementControllerOperation.UpdateMemberPin
    public ResponseEntity<Void> updateMemberPin(
        @RequestBody @Valid GroupCardMemberUpdatePinRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = controller.getBankCard(request, token, MANAGE_GROUP_CARD_MEMBERS);

        if (!CardType.GROUP.equals(card.getType())) {
            throw CardException.NOT_GROUP_CARD;
        }

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        final boolean isAdmin = getTokenService().hasAuthority(token.token(), MANAGE_GROUP_CARD_MEMBERS);
        if (!isAdmin && !getPinAdapter().pinMatches(request.getPin(), card.getPin())) {
            throw CardException.INVALID_PIN;
        }

        final GameAccount memberGameAccount = getGameAccountService().getGameAccount(request.getMemberGameAccount());

        final Optional<BankCardMember> member = bankCardMemberDAO.find(
            new BankCardMemberSearchDTO().setCard(card).setGameAccount(memberGameAccount)
        );
        if (member.isEmpty()) {
            throw CardException.UNKNOWN_MEMBER;
        }

        member.get().setPin(getPinAdapter().encodePin(request.getNewPin()));
        bankCardMemberDAO.save(member.get());
        getCardHistoryService().writeUpdateGroupCardMemberPin(card);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = GROUP_MEMBER_SETTING_UPDATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownCardOperation
    @NotGroupCardOperation
    @UnknownMemberOperation
    @CardDisabledOperation
    @InvalidPinOperation
    @GroupCardManagementControllerOperation.UpdateMemberSetting
    public ResponseEntity<Void> updateMemberSetting(
        @RequestBody @Valid GroupCardMemberSettingUpdateRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = controller.getBankCard(request, token, MANAGE_GROUP_CARD_MEMBERS);

        if (!CardType.GROUP.equals(card.getType())) {
            throw CardException.NOT_GROUP_CARD;
        }

        if (card.isDisabled()) {
            throw CardException.CARD_DISABLED;
        }

        final boolean isAdmin = getTokenService().hasAuthority(token.token(), MANAGE_GROUP_CARD_MEMBERS);
        if (!isAdmin && !getPinAdapter().pinMatches(request.getPin(), card.getPin())) {
            throw CardException.INVALID_PIN;
        }

        final GameAccount memberGameAccount = getGameAccountService().getGameAccount(request.getMemberGameAccount());

        final Optional<BankCardMember> member = bankCardMemberDAO.find(
            new BankCardMemberSearchDTO().setCard(card).setGameAccount(memberGameAccount)
        );
        if (member.isEmpty()) {
            throw CardException.UNKNOWN_MEMBER;
        }

        final Optional<BankCardMemberSetting> existing = bankCardMemberSettingDAO.find(
            new BankCardMemberSettingSearchDTO().setBankCardMember(member.get()).setType(request.getType())
        );

        if (existing.isPresent()) {
            existing.get().setValue(request.getValue());
            bankCardMemberSettingDAO.save(existing.get());
        } else {
            final BankCardMemberSetting setting = new BankCardMemberSetting();
            setting.setCard(card);
            setting.setBankCardMember(member.get());
            setting.setType(request.getType());
            setting.setValue(request.getValue());
            bankCardMemberSettingDAO.save(setting);
        }

        getCardHistoryService().writeUpdateGroupCardMemberSetting(card);

        return ResponseEntity.ok().build();
    }

    public GroupCardManagementController getController() {
        return controller;
    }
}
