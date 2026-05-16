package me.statuxia.shulkerapi.controller.api.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.request.GroupCardMemberListRequest;
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
class GroupCardViewControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";
    private static final String GROUP_CARD_NUMBER = "1111 0000";

    @Autowired
    protected BankCardDAO bankCardDAO;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    void listMembersTest() throws Exception {
        final GroupCardMemberListRequest request = new GroupCardMemberListRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + GroupCardViewController.GROUP_MEMBER_LIST)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(1))
            .andExpect(jsonPath("$.items[0].gameAccount").value("member-one"));
    }

    @Test
    void listMembersDisabledCardTest() throws Exception {
        bankCardDAO.findByNumber(GROUP_CARD_NUMBER).ifPresent(card -> {
            card.setDisabled(true);
            bankCardDAO.save(card);
        });

        final GroupCardMemberListRequest request = new GroupCardMemberListRequest();
        request.setGameAccount("owner");
        request.setCardNumber(GROUP_CARD_NUMBER);

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + GroupCardViewController.GROUP_MEMBER_LIST)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1405"));
    }
}
