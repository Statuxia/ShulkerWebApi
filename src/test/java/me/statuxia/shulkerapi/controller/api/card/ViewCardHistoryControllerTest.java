package me.statuxia.shulkerapi.controller.api.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.BankCardHistoryDAO;
import me.statuxia.shulkerapi.model.BankCardHistory;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import me.statuxia.shulkerapi.request.CardHistoryRequest;
import me.statuxia.shulkerapi.response.BankCardHistoryPaginationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
    "classpath:sql/CardControllerTest.sql",
    "classpath:sql/CardHistoryControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
class ViewCardHistoryControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";

    @Autowired
    protected ViewCardHistoryController controller;

    @Autowired
    protected BankCardHistoryDAO bankCardHistoryDAO;

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void listTest() throws Exception {
        final CardHistoryRequest request = new CardHistoryRequest();
        request.setCardNumber("1234 5678");
        request.setGameAccount("test-name");

        final List<BankCardHistory> list = bankCardHistoryDAO.findAllById(List.of(1L, 2L, 3L, 4L, 5L, 6L));
        final BankCardHistoryPaginationResponse response = new BankCardHistoryPaginationResponse();
        response.setTotal(list.size());
        response.setItems(list.stream().map(controller::map).toList());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + ViewCardHistoryController.LIST)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(
            response,
            objectMapper.readValue(result.getResponse().getContentAsString(), BankCardHistoryPaginationResponse.class)
        );
    }

    @Test
    void listFilterTest() throws Exception {

        final CardHistoryRequest request = new CardHistoryRequest();
        request.setCardNumber("1234 5678");
        request.setGameAccount("test-name");
        request.setTypes(List.of(BankCardHistoryType.DEPOSIT, BankCardHistoryType.WITHDRAW));

        final List<BankCardHistory> list = bankCardHistoryDAO.findAllById(List.of(5L, 6L));
        final BankCardHistoryPaginationResponse response = new BankCardHistoryPaginationResponse();
        response.setTotal(list.size());
        response.setItems(list.stream().map(controller::map).toList());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + ViewCardHistoryController.LIST)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(
            response,
            objectMapper.readValue(result.getResponse().getContentAsString(), BankCardHistoryPaginationResponse.class)
        );
    }

    @Test
    void listWrongAccountTest() throws Exception {
        final CardHistoryRequest request = new CardHistoryRequest();
        request.setGameAccount("test-name-2");
        request.setCardNumber("1234 5678");

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + ViewCardHistoryController.LIST)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1401"));
    }
}