package me.statuxia.shulkerapi.controller.api.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardHistorySearchDTO;
import me.statuxia.shulkerapi.model.BankCardHistory;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import me.statuxia.shulkerapi.model.CardType;
import me.statuxia.shulkerapi.request.CardCreateRequest;
import me.statuxia.shulkerapi.request.CardUpdatePinRequest;
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

import java.util.List;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
@Rollback
class CardManagementControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";
    private static final String SESSION_TOKEN_2 = "session-token-2";

    @Autowired
    protected BankCardDAO bankCardDAO;

    @Autowired
    protected BankCardHistoryDAO bankCardHistoryDAO;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    void createFirstCardTest() throws Exception {
        final CardCreateRequest request = new CardCreateRequest();
        request.setGameAccount("test-name-3");
        request.setType(CardType.DIRECT);
        request.setPin("1234");

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardManagementController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.number").value(matchesPattern("\\d{4} \\d{4}")));
        assertEquals(BankCardHistoryType.CREATE_CARD, bankCardHistoryDAO.findList(new BankCardHistorySearchDTO()).getLast().getType());
    }

    @Test
    void createTooManyCardsTest() throws Exception {
        final CardCreateRequest request = new CardCreateRequest();
        request.setGameAccount("test-name-2");
        request.setType(CardType.DIRECT);
        request.setPin("1234");

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardManagementController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN_2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1404"));
    }

    @Test
    void createNotEnoughFundsTest() throws Exception {
        final CardCreateRequest request = new CardCreateRequest();
        request.setGameAccount("test-name");
        request.setType(CardType.DIRECT);
        request.setPin("1234");
        request.setPaymentCardNumber("1234 5678");

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardManagementController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1501"));
    }

    @Test
    void createDisabledPaymentCardTest() throws Exception {
        final CardCreateRequest request = new CardCreateRequest();
        request.setGameAccount("test-name");
        request.setType(CardType.DIRECT);
        request.setPin("1234");
        request.setPaymentCardNumber("1234 5678");

        bankCardDAO.findByNumber("1234 5678").ifPresent(card -> {
            card.setDisabled(true);
            bankCardDAO.save(card);
        });

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardManagementController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1406"));
    }

    @Test
    void createWithNullPaymentTest() throws Exception {
        final CardCreateRequest request = new CardCreateRequest();
        request.setGameAccount("test-name");
        request.setType(CardType.DIRECT);
        request.setPin("1234");

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardManagementController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1402"));
    }

    @Test
    void createWithUnknownPaymentTest() throws Exception {
        final CardCreateRequest request = new CardCreateRequest();
        request.setGameAccount("test-name");
        request.setType(CardType.DIRECT);
        request.setPin("1234");
        request.setPaymentCardNumber("1234 5555");

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardManagementController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1402"));
    }

    @Test
    void createWithPaymentTest() throws Exception {
        final CardCreateRequest request = new CardCreateRequest();
        request.setGameAccount("test-name");
        request.setType(CardType.DIRECT);
        request.setPin("1234");
        request.setPaymentCardNumber("1234 5678");

        bankCardDAO.findByNumber("1234 5678").ifPresent(card -> {
            card.setCurrency(1000L);
            bankCardDAO.save(card);
        });

        mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardManagementController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.number").value(matchesPattern("\\d{4} \\d{4}")));

        final List<BankCardHistory> histories = bankCardHistoryDAO.findList(new BankCardHistorySearchDTO());
        assertEquals(BankCardHistoryType.WITHDRAW, histories.get(histories.size() - 2).getType());
        assertEquals(BankCardHistoryType.CREATE_CARD, histories.get(histories.size() - 1).getType());
    }

    @Test
    void updatePinTest() throws Exception {
        final CardUpdatePinRequest request = new CardUpdatePinRequest();
        request.setGameAccount("test-name");
        request.setNewPin("1235");
        request.setCardNumber("1234 5678");
        request.setPin("1234");

        mockMvc.perform(
            MockMvcRequestBuilders.put(CardController.PREFIX + CardManagementController.UPDATE_PIN)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());
        assertEquals(BankCardHistoryType.UPDATE_PIN, bankCardHistoryDAO.findList(new BankCardHistorySearchDTO()).getLast().getType());
    }

    @Test
    void updateWrongOwnerTest() throws Exception {
        final CardUpdatePinRequest request = new CardUpdatePinRequest();
        request.setGameAccount("test-name-3");
        request.setNewPin("1235");
        request.setCardNumber("1234 5678");
        request.setPin("1234");

        mockMvc.perform(
                MockMvcRequestBuilders.put(CardController.PREFIX + CardManagementController.UPDATE_PIN)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1401"));
    }

    @Test
    void updateWrongPinTest() throws Exception {
        final CardUpdatePinRequest request = new CardUpdatePinRequest();
        request.setGameAccount("test-name");
        request.setNewPin("1234");
        request.setCardNumber("1234 5678");
        request.setPin("1235");

        mockMvc.perform(
                MockMvcRequestBuilders.put(CardController.PREFIX + CardManagementController.UPDATE_PIN)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1403"));
    }

    @Test
    void updateDisabledTest() throws Exception {
        final CardUpdatePinRequest request = new CardUpdatePinRequest();
        request.setGameAccount("test-name");
        request.setNewPin("1235");
        request.setCardNumber("1234 5678");
        request.setPin("1234");

        bankCardDAO.findByNumber("1234 5678").ifPresent(card -> {
            card.setDisabled(true);
            bankCardDAO.save(card);
        });

        mockMvc.perform(
                MockMvcRequestBuilders.put(CardController.PREFIX + CardManagementController.UPDATE_PIN)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1405"));
    }
}