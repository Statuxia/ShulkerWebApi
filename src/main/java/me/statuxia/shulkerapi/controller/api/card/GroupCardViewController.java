package me.statuxia.shulkerapi.controller.api.card;

import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.configuration.properties.CardProperties;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardMemberDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.search.impl.BankCardMemberSearchDTO;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardMember;
import me.statuxia.shulkerapi.model.CardType;
import me.statuxia.shulkerapi.request.GroupCardMemberListRequest;
import me.statuxia.shulkerapi.response.GroupCardMemberItem;
import me.statuxia.shulkerapi.response.GroupCardMemberPaginationResponse;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.service.impl.MessageService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.GroupCardViewControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.card.NotGroupCardOperation;
import me.statuxia.shulkerapi.swagger.controller.card.UnknownCardOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static me.statuxia.shulkerapi.model.TokenAuthorityEnum.LIST_GROUP_CARD_MEMBERS;

@RestController
@RequestMapping(value = CardController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
public class GroupCardViewController extends CardController {

    public static final String GROUP_MEMBER_LIST = "/group/member/list";

    private final BankCardMemberDAO bankCardMemberDAO;
    private final GroupCardViewController controller;

    @Autowired
    public GroupCardViewController(
        TokenService tokenService, GameAccountService gameAccountService, BankCardDAO bankCardDAO,
        CardProperties cardProperties, OperationProcessorService operationProcessorService,
        CardHistoryService cardHistoryService, MessageService messageService,
        BankCardMemberDAO bankCardMemberDAO
    ) {
        super(
            tokenService, gameAccountService, bankCardDAO, cardProperties,
            operationProcessorService, cardHistoryService, messageService
        );
        this.bankCardMemberDAO = bankCardMemberDAO;
        this.controller = this;
    }

    @PostMapping(value = GROUP_MEMBER_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownCardOperation
    @NotGroupCardOperation
    @GroupCardViewControllerOperation.ListMembers
    public ResponseEntity<GroupCardMemberPaginationResponse> listMembers(
        @RequestBody @Valid GroupCardMemberListRequest request,
        @AuthData TokenData token
    ) {
        final BankCard card = controller.getBankCard(request, token, LIST_GROUP_CARD_MEMBERS);

        if (!CardType.GROUP.equals(card.getType())) {
            throw CardException.NOT_GROUP_CARD;
        }

        final BankCardMemberSearchDTO dto = new BankCardMemberSearchDTO()
            .setCard(card)
            .setPageable(request.getPageable());

        final GroupCardMemberPaginationResponse response = new GroupCardMemberPaginationResponse();
        response.setTotal(bankCardMemberDAO.count(dto));
        response.setItems(bankCardMemberDAO.findList(dto).stream().map(this::buildMemberItem).toList());

        return ResponseEntity.ok(response);
    }

    private GroupCardMemberItem buildMemberItem(BankCardMember member) {
        return new GroupCardMemberItem()
            .setId(member.getId())
            .setGameAccount(member.getGameAccount() == null ? null : member.getGameAccount().getName())
            .setAddedAt(member.getAddedAt().getMillis())
            .setCredited(member.getCredited())
            .setDebited(member.getDebited());
    }

    public GroupCardViewController getController() {
        return controller;
    }
}
