package me.statuxia.shulkerapi.controller.api.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardMemberDAO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardHistorySearchDTO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardMemberSearchDTO;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import me.statuxia.shulkerapi.model.BankCardSettingType;
import me.statuxia.shulkerapi.request.GroupCardMemberAddRequest;
import me.statuxia.shulkerapi.request.GroupCardMemberRemoveRequest;
import me.statuxia.shulkerapi.request.GroupCardMemberSettingUpdateRequest;
import me.statuxia.shulkerapi.request.GroupCardMemberUpdatePinRequest;
import me.statuxia.shulkerapi.request.GroupCardSettingUpdateRequest;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
    "classpath:sql/GroupCardControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
@Rollback
class GroupCardManagementControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";
    private static final String GROUP_CARD_NUMBER = "1111 0000";
    private static final String GROUP_CARD_PIN = "1234";

    @Autowired
    protected BankCardDAO bankCardDAO;

    @Autowired
    protected BankCardHistoryDAO bankCardHistoryDAO;

    @Autowired
    protected BankCardMemberDAO bankCardMemberDAO;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    // updateSetting tests

    @Test
    void updateSettingTest() throws Exception {
        final GroupCardSettingUpdateRequest request = new GroupCardSettingUpdateRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin(GROUP_CARD_PIN);
        request.setType(BankCardSettingType.TRANSFER_PERMISSION);
        request.setValue("ALL");

        mockMvc.perform(
            MockMvcRequestBuilders.put(CardController.PREFIX + GroupCardManagementController.GROUP_SETTING_UPDATE)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        final var card = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        assertEquals(
            BankCardHistoryType.UPDATE_GROUP_CARD_SETTING,
            bankCardHistoryDAO.findList(new BankCardHistorySearchDTO().setCard(card)).getLast().getType()
        );
    }

    @Test
    void updateSettingDisabledCardTest() throws Exception {
        bankCardDAO.findByNumber(GROUP_CARD_NUMBER).ifPresent(card -> {
            card.setDisabled(true);
            bankCardDAO.save(card);
        });

        final GroupCardSettingUpdateRequest request = new GroupCardSettingUpdateRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin(GROUP_CARD_PIN);
        request.setType(BankCardSettingType.TRANSFER_PERMISSION);
        request.setValue("ALL");

        mockMvc.perform(
                MockMvcRequestBuilders.put(CardController.PREFIX + GroupCardManagementController.GROUP_SETTING_UPDATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1405"));
    }

    @Test
    void updateSettingWrongPinTest() throws Exception {
        final GroupCardSettingUpdateRequest request = new GroupCardSettingUpdateRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin("0000");
        request.setType(BankCardSettingType.TRANSFER_PERMISSION);
        request.setValue("ALL");

        mockMvc.perform(
                MockMvcRequestBuilders.put(CardController.PREFIX + GroupCardManagementController.GROUP_SETTING_UPDATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1403"));
    }

    // addMember tests

    @Test
    void addMemberTest() throws Exception {
        final GroupCardMemberAddRequest request = new GroupCardMemberAddRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setCardPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("other-player");
        request.setPin("5678");

        mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_ADD)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        final var card = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        assertEquals(2L, bankCardMemberDAO.count(new BankCardMemberSearchDTO().setCard(card)));
        assertEquals(
            BankCardHistoryType.ADD_GROUP_CARD_MEMBER,
            bankCardHistoryDAO.findList(new BankCardHistorySearchDTO().setCard(card)).getLast().getType()
        );
    }

    @Test
    void addMemberDisabledCardTest() throws Exception {
        bankCardDAO.findByNumber(GROUP_CARD_NUMBER).ifPresent(card -> {
            card.setDisabled(true);
            bankCardDAO.save(card);
        });

        final GroupCardMemberAddRequest request = new GroupCardMemberAddRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setCardPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("other-player");
        request.setPin("5678");

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_ADD)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1405"));
    }

    @Test
    void addMemberWrongPinTest() throws Exception {
        final GroupCardMemberAddRequest request = new GroupCardMemberAddRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setCardPin("0000");
        request.setMemberGameAccount("other-player");
        request.setPin("5678");

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_ADD)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1403"));
    }

    @Test
    void addMemberOwnerIsSelfTest() throws Exception {
        final GroupCardMemberAddRequest request = new GroupCardMemberAddRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setCardPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("owner");
        request.setPin("5678");

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_ADD)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1418"));
    }

    // removeMember tests

    @Test
    void removeMemberTest() throws Exception {
        final GroupCardMemberRemoveRequest request = new GroupCardMemberRemoveRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("member-one");

        mockMvc.perform(
            MockMvcRequestBuilders.delete(CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_REMOVE)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        final var card = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        assertEquals(0L, bankCardMemberDAO.count(new BankCardMemberSearchDTO().setCard(card)));
        assertEquals(
            BankCardHistoryType.REMOVE_GROUP_CARD_MEMBER,
            bankCardHistoryDAO.findList(new BankCardHistorySearchDTO().setCard(card)).getLast().getType()
        );
    }

    @Test
    void removeMemberDisabledCardTest() throws Exception {
        bankCardDAO.findByNumber(GROUP_CARD_NUMBER).ifPresent(card -> {
            card.setDisabled(true);
            bankCardDAO.save(card);
        });

        final GroupCardMemberRemoveRequest request = new GroupCardMemberRemoveRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("member-one");

        mockMvc.perform(
                MockMvcRequestBuilders.delete(CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_REMOVE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1405"));
    }

    @Test
    void removeMemberWrongPinTest() throws Exception {
        final GroupCardMemberRemoveRequest request = new GroupCardMemberRemoveRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin("0000");
        request.setMemberGameAccount("member-one");

        mockMvc.perform(
                MockMvcRequestBuilders.delete(CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_REMOVE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1403"));
    }

    @Test
    void removeMemberUnknownMemberTest() throws Exception {
        final GroupCardMemberRemoveRequest request = new GroupCardMemberRemoveRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("other-player");

        mockMvc.perform(
                MockMvcRequestBuilders.delete(CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_REMOVE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1417"));
    }

    // updateMemberPin tests

    @Test
    void updateMemberPinTest() throws Exception {
        final GroupCardMemberUpdatePinRequest request = new GroupCardMemberUpdatePinRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("member-one");
        request.setNewPin("9999");

        mockMvc.perform(
            MockMvcRequestBuilders.put(CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_UPDATE_PIN)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        final var card = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        final var member = bankCardMemberDAO.find(new BankCardMemberSearchDTO().setCard(card));
        assertTrue(member.isPresent());
        assertEquals("9999", member.get().getPin());
        assertEquals(
            BankCardHistoryType.UPDATE_GROUP_CARD_MEMBER_PIN,
            bankCardHistoryDAO.findList(new BankCardHistorySearchDTO().setCard(card)).getLast().getType()
        );
    }

    @Test
    void updateMemberPinDisabledCardTest() throws Exception {
        bankCardDAO.findByNumber(GROUP_CARD_NUMBER).ifPresent(card -> {
            card.setDisabled(true);
            bankCardDAO.save(card);
        });

        final GroupCardMemberUpdatePinRequest request = new GroupCardMemberUpdatePinRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("member-one");
        request.setNewPin("9999");

        mockMvc.perform(
                MockMvcRequestBuilders.put(
                        CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_UPDATE_PIN
                    )
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1405"));
    }

    @Test
    void updateMemberPinWrongPinTest() throws Exception {
        final GroupCardMemberUpdatePinRequest request = new GroupCardMemberUpdatePinRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin("0000");
        request.setMemberGameAccount("member-one");
        request.setNewPin("9999");

        mockMvc.perform(
                MockMvcRequestBuilders.put(
                        CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_UPDATE_PIN
                    )
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1403"));
    }

    @Test
    void updateMemberPinUnknownMemberTest() throws Exception {
        final GroupCardMemberUpdatePinRequest request = new GroupCardMemberUpdatePinRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("other-player");
        request.setNewPin("9999");

        mockMvc.perform(
                MockMvcRequestBuilders.put(
                        CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_UPDATE_PIN
                    )
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1417"));
    }

    // updateMemberSetting tests

    @Test
    void updateMemberSettingTest() throws Exception {
        final GroupCardMemberSettingUpdateRequest request = new GroupCardMemberSettingUpdateRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("member-one");
        request.setType(BankCardSettingType.DEPOSIT_PERMISSION);
        request.setValue("MEMBERS");

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                    CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_SETTING_UPDATE
                )
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        final var card = bankCardDAO.findByNumber(GROUP_CARD_NUMBER).get();
        assertEquals(
            BankCardHistoryType.UPDATE_GROUP_CARD_MEMBER_SETTING,
            bankCardHistoryDAO.findList(new BankCardHistorySearchDTO().setCard(card)).getLast().getType()
        );
    }

    @Test
    void updateMemberSettingDisabledCardTest() throws Exception {
        bankCardDAO.findByNumber(GROUP_CARD_NUMBER).ifPresent(card -> {
            card.setDisabled(true);
            bankCardDAO.save(card);
        });

        final GroupCardMemberSettingUpdateRequest request = new GroupCardMemberSettingUpdateRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("member-one");
        request.setType(BankCardSettingType.DEPOSIT_PERMISSION);
        request.setValue("MEMBERS");

        mockMvc.perform(
                MockMvcRequestBuilders.put(
                        CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_SETTING_UPDATE
                    )
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1405"));
    }

    @Test
    void updateMemberSettingWrongPinTest() throws Exception {
        final GroupCardMemberSettingUpdateRequest request = new GroupCardMemberSettingUpdateRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin("0000");
        request.setMemberGameAccount("member-one");
        request.setType(BankCardSettingType.DEPOSIT_PERMISSION);
        request.setValue("MEMBERS");

        mockMvc.perform(
                MockMvcRequestBuilders.put(
                        CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_SETTING_UPDATE
                    )
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1403"));
    }

    @Test
    void updateMemberSettingUnknownMemberTest() throws Exception {
        final GroupCardMemberSettingUpdateRequest request = new GroupCardMemberSettingUpdateRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);
        request.setPin(GROUP_CARD_PIN);
        request.setMemberGameAccount("other-player");
        request.setType(BankCardSettingType.DEPOSIT_PERMISSION);
        request.setValue("MEMBERS");

        mockMvc.perform(
                MockMvcRequestBuilders.put(
                        CardController.PREFIX + GroupCardManagementController.GROUP_MEMBER_SETTING_UPDATE
                    )
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1417"));
    }
}
