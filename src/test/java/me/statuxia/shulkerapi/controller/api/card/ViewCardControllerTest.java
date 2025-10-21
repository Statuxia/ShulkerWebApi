package me.statuxia.shulkerapi.controller.api.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.CardStyleType;
import me.statuxia.shulkerapi.request.BaseCardRequest;
import me.statuxia.shulkerapi.request.CardGetRequest;
import me.statuxia.shulkerapi.request.CardRequest;
import me.statuxia.shulkerapi.response.BankCardItem;
import me.statuxia.shulkerapi.response.BankCardPaginationResponse;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
    "classpath:sql/CardControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
class ViewCardControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";

    @Autowired
    protected BankCardDAO bankCardDAO;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    void getTest() throws Exception {
        final CardGetRequest request = new CardGetRequest();
        request.setCardNumber("1234 5678");
        request.setGameAccount("test-name");

        final BankCard card = bankCardDAO.findById(1L).get();
        final DateTime createTime = card.getCreateTime();
        final DateTime disabledTime = card.getDisabledTime();

        final BankCardItem response = new BankCardItem()
            .setId(1L)
            .setCardNumber("1234 5678")
            .setCurrency(0L)
            .setCreateTime(createTime.getMillis())
            .setDisabled(false)
            .setDisabledTime(disabledTime == null ? null : disabledTime.getMillis())
            .setGameAccount("test-name")
            .setStyle(CardStyleType.DEFAULT)
            .setPatternSeed(0L);

        assertTrue(bankCardDAO.findByNumber("1234 5678").isPresent());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + ViewCardController.GET)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(response), result.getResponse().getContentAsString());
    }

    @ParameterizedTest
    @CsvSource({"test-name-2", "test-name-1"})
    void getNotOwnedTest(String name) throws Exception {
        final CardGetRequest request = new CardGetRequest();
        request.setCardNumber("1234 5678");
        request.setGameAccount(name);

        final BankCardItem response = new BankCardItem()
            .setId(1L)
            .setCardNumber("1234 5678")
            .setGameAccount("test-name")
            .setStyle(CardStyleType.DEFAULT)
            .setPatternSeed(0L);

        assertTrue(bankCardDAO.findByNumber("1234 5678").isPresent());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + ViewCardController.GET)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(response), result.getResponse().getContentAsString());
    }

    @ParameterizedTest
    @CsvSource({"badFormat", "0000 0000"})
    void getBadCardsTest(String card) throws Exception {
        final CardGetRequest request = new CardGetRequest();
        request.setCardNumber(card);

        mockMvc.perform(
            MockMvcRequestBuilders.post(CardController.PREFIX + ViewCardController.GET)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void listTest() throws Exception {
        final CardRequest request = new BaseCardRequest();
        request.setGameAccount("test-name");

        final BankCard card = bankCardDAO.findById(1L).get();
        final DateTime createTime = card.getCreateTime();
        final DateTime disabledTime = card.getDisabledTime();

        final BankCardPaginationResponse response = new BankCardPaginationResponse();
        response.setTotal(1);
        response.setItems(List.of(
            new BankCardItem()
                .setId(1L)
                .setCardNumber("1234 5678")
                .setCurrency(0L)
                .setCreateTime(createTime.getMillis())
                .setDisabled(false)
                .setDisabledTime(disabledTime == null ? null : disabledTime.getMillis())
                .setGameAccount("test-name")
                .setStyle(CardStyleType.DEFAULT)
                .setPatternSeed(0L)
        ));

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + ViewCardController.LIST)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(response), result.getResponse().getContentAsString());
    }

    @Test
    void listWrongAccountTest() throws Exception {
        final CardRequest request = new BaseCardRequest();
        request.setGameAccount("test-name-2");

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + ViewCardController.LIST)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1201"));
    }
}