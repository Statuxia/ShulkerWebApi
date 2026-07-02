package me.statuxia.shulkerapi.controller.api.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardMemberDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardSettingDAO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardHistorySearchDTO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardMemberSearchDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import me.statuxia.shulkerapi.model.BankCardMember;
import me.statuxia.shulkerapi.model.BankCardSetting;
import me.statuxia.shulkerapi.model.BankCardSettingType;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.request.ChangeCardBalanceRequest;
import me.statuxia.shulkerapi.request.TransferFundsRequest;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.PinAdapter;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
    "classpath:sql/GroupCardActionControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
@Rollback
class GroupCardActionControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";
    private static final String GROUP_CARD_NUMBER = "1111 0000";
    private static final String GROUP_CARD_PIN = "1234";
    private static final String MEMBER_PIN = "0001";
    private static final String DIRECT_CARD_OTHER = "3333 0000";

    @Autowired
    protected BankCardDAO bankCardDAO;

    @Autowired
    protected BankCardHistoryDAO bankCardHistoryDAO;

    @Autowired
    protected BankCardMemberDAO bankCardMemberDAO;

    @Autowired
    protected BankCardSettingDAO bankCardSettingDAO;

    @Autowired
    protected GameAccountService gameAccountService;

    @Autowired
    protected PinAdapter pinAdapter;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    // deposit tests

    @Test
    void groupDepositOwnerTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setGameAccount("owner");
        request.setFunds(200L);

        final BankCard card = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        assertEquals(1000L, card.getCurrency());

        mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.DEPOSIT)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(1200L, card.getCurrency());
        final BankCardHistoryType historyType = bankCardHistoryDAO
            .findList(new BankCardHistorySearchDTO().setCard(card)).getLast().getType();
        assertEquals(BankCardHistoryType.DEPOSIT, historyType);
    }

    @Test
    void groupDepositMemberTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setGameAccount("owner");
        request.setFunds(300L);
        request.setMemberGameAccount("member-one");

        final BankCard card = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();

        mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.DEPOSIT)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(1300L, card.getCurrency());

        final GameAccount memberGA = gameAccountService.getGameAccount("member-one");
        final BankCardMember member = bankCardMemberDAO.find(
            new BankCardMemberSearchDTO().setCard(card).setGameAccount(memberGA)
        ).get();
        assertEquals(300L, member.getCredited());

        final BankCardHistoryType historyType = bankCardHistoryDAO
            .findList(new BankCardHistorySearchDTO().setCard(card)).getLast().getType();
        assertEquals(BankCardHistoryType.DEPOSIT, historyType);
    }

    @Test
    void groupDepositUnknownMemberTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setGameAccount("owner");
        request.setFunds(100L);
        request.setMemberGameAccount("other-player");

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.DEPOSIT)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1417"));
    }

    @Test
    void groupDepositMemberPermissionDeniedTest() throws Exception {
        final BankCard card = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        final BankCardSetting setting = new BankCardSetting();
        setting.setCard(card);
        setting.setType(BankCardSettingType.DEPOSIT_PERMISSION);
        setting.setValue("OWNER");
        bankCardSettingDAO.save(setting);

        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setGameAccount("owner");
        request.setFunds(100L);
        request.setMemberGameAccount("member-one");

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.DEPOSIT)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1420"));
    }

    // withdraw tests

    @Test
    void groupWithdrawOwnerTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setGameAccount("owner");
        request.setFunds(500L);
        request.setPin(GROUP_CARD_PIN);

        final BankCard card = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();

        mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(500L, card.getCurrency());
        final BankCardHistoryType historyType = bankCardHistoryDAO
            .findList(new BankCardHistorySearchDTO().setCard(card)).getLast().getType();
        assertEquals(BankCardHistoryType.WITHDRAW, historyType);
    }

    @Test
    void groupWithdrawOwnerWrongPinTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setGameAccount("owner");
        request.setFunds(100L);
        request.setPin("9999");

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1403"));
    }

    @Test
    void groupWithdrawMemberTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setGameAccount("owner");
        request.setFunds(400L);
        request.setPin(MEMBER_PIN);
        request.setMemberGameAccount("member-one");

        final BankCard card = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();

        mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(600L, card.getCurrency());

        final GameAccount memberGA = gameAccountService.getGameAccount("member-one");
        final BankCardMember member = bankCardMemberDAO.find(
            new BankCardMemberSearchDTO().setCard(card).setGameAccount(memberGA)
        ).get();
        assertEquals(400L, member.getDebited());

        final BankCardHistoryType historyType = bankCardHistoryDAO
            .findList(new BankCardHistorySearchDTO().setCard(card)).getLast().getType();
        assertEquals(BankCardHistoryType.WITHDRAW, historyType);
    }

    @Test
    void groupWithdrawMemberWrongPinTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setGameAccount("owner");
        request.setFunds(100L);
        request.setPin("9999");
        request.setMemberGameAccount("member-one");

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1403"));
    }

    @Test
    void groupWithdrawMemberPermissionDeniedTest() throws Exception {
        final BankCard card = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        final BankCardSetting setting = new BankCardSetting();
        setting.setCard(card);
        setting.setType(BankCardSettingType.WITHDRAW_PERMISSION);
        setting.setValue("OWNER");
        bankCardSettingDAO.save(setting);

        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setGameAccount("owner");
        request.setFunds(100L);
        request.setPin(MEMBER_PIN);
        request.setMemberGameAccount("member-one");

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1420"));
    }

    // transfer tests

    @Test
    void transferFromGroupCardTest() throws Exception {
        final TransferFundsRequest request = new TransferFundsRequest();
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setGameAccount("owner");
        request.setFunds(100L);
        request.setPin(GROUP_CARD_PIN);
        request.setReceiverCard(DIRECT_CARD_OTHER);

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.TRANSFER)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1419"));
    }

    @Test
    void transferToGroupCardDefaultPermissionTest() throws Exception {
        final TransferFundsRequest request = new TransferFundsRequest();
        request.setCardNumber(DIRECT_CARD_OTHER);
        request.setGameAccount("other-player");
        request.setFunds(100L);
        request.setPin("0000");
        request.setReceiverCard(GROUP_CARD_NUMBER);

        final BankCard groupCard = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();

        mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.TRANSFER)
                .header(X_TOKEN_HEADER, "session-token-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(1100L, groupCard.getCurrency());
    }

    @Test
    void transferToGroupCardMembersPermissionAllowedTest() throws Exception {
        final BankCard groupCard = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        final BankCardSetting setting = new BankCardSetting();
        setting.setCard(groupCard);
        setting.setType(BankCardSettingType.TRANSFER_PERMISSION);
        setting.setValue("MEMBERS");
        bankCardSettingDAO.save(setting);

        final BankCard memberDirectCard = bankCardDAO.findByNumber("2222 0000").get();
        final TransferFundsRequest request = new TransferFundsRequest();
        request.setCardNumber(memberDirectCard.getNumber());
        request.setGameAccount("member-one");
        request.setFunds(100L);
        request.setPin("4321");
        request.setReceiverCard(GROUP_CARD_NUMBER);

        mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.TRANSFER)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(1100L, groupCard.getCurrency());
    }

    @Test
    void transferToGroupCardMembersPermissionDeniedTest() throws Exception {
        final BankCard groupCard = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        final BankCardSetting setting = new BankCardSetting();
        setting.setCard(groupCard);
        setting.setType(BankCardSettingType.TRANSFER_PERMISSION);
        setting.setValue("MEMBERS");
        bankCardSettingDAO.save(setting);

        final TransferFundsRequest request = new TransferFundsRequest();
        request.setCardNumber(DIRECT_CARD_OTHER);
        request.setGameAccount("other-player");
        request.setFunds(100L);
        request.setPin("0000");
        request.setReceiverCard(GROUP_CARD_NUMBER);

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.TRANSFER)
                .header(X_TOKEN_HEADER, "session-token-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1420"));
    }

    @Test
    void transferToGroupCardOwnerPermissionAllowedTest() throws Exception {
        final BankCard groupCard = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        final BankCardSetting setting = new BankCardSetting();
        setting.setCard(groupCard);
        setting.setType(BankCardSettingType.TRANSFER_PERMISSION);
        setting.setValue("OWNER");
        bankCardSettingDAO.save(setting);

        final BankCard memberDirectCard = bankCardDAO.findByNumber("2222 0000").get();
        // member-one is game_account of account 1 (same as owner), but they own "2222 0000"
        // We need a DIRECT card owned by "owner" to transfer FROM owner
        // In our SQL: "2222 0000" is owned by "member-one", not "owner"
        // Let's create a direct card for owner temporarily
        final BankCard ownerDirectCard = new BankCard();
        ownerDirectCard.setNumber("9999 9999");
        ownerDirectCard.setGameAccount(gameAccountService.getGameAccount("owner"));
        ownerDirectCard.setPin(pinAdapter.encodePin(GROUP_CARD_PIN));
        ownerDirectCard.setType(me.statuxia.shulkerapi.model.CardType.DIRECT);
        ownerDirectCard.setCurrency(200L);
        ownerDirectCard.setCreateTime(DateTime.now());
        bankCardDAO.save(ownerDirectCard);

        final TransferFundsRequest request = new TransferFundsRequest();
        request.setCardNumber("9999 9999");
        request.setGameAccount("owner");
        request.setFunds(100L);
        request.setPin(GROUP_CARD_PIN);
        request.setReceiverCard(GROUP_CARD_NUMBER);

        mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.TRANSFER)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(1100L, groupCard.getCurrency());
    }

    @Test
    void transferToGroupCardOwnerPermissionDeniedTest() throws Exception {
        final BankCard groupCard = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        final BankCardSetting setting = new BankCardSetting();
        setting.setCard(groupCard);
        setting.setType(BankCardSettingType.TRANSFER_PERMISSION);
        setting.setValue("OWNER");
        bankCardSettingDAO.save(setting);

        final TransferFundsRequest request = new TransferFundsRequest();
        request.setCardNumber(DIRECT_CARD_OTHER);
        request.setGameAccount("other-player");
        request.setFunds(100L);
        request.setPin("0000");
        request.setReceiverCard(GROUP_CARD_NUMBER);

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.TRANSFER)
                .header(X_TOKEN_HEADER, "session-token-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1420"));
    }
}
