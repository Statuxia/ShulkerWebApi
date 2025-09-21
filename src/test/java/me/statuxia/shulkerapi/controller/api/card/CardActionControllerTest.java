package me.statuxia.shulkerapi.controller.api.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardHistorySearchDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistory;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import me.statuxia.shulkerapi.request.ChangeCardBalanceRequest;
import me.statuxia.shulkerapi.request.TransferFundsRequest;
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

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.*;
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
class CardActionControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";

    @Autowired
    protected BankCardDAO bankCardDAO;

    @Autowired
    protected BankCardHistoryDAO bankCardHistoryDAO;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    void depositTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(100L);
        request.setGameAccount("test-name");

        final BankCard card = bankCardDAO.findByNumber("1234 5678").get();
        assertEquals(0, (long) card.getCurrency());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.DEPOSIT)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals("", result.getResponse().getContentAsString());
        assertEquals(100, (long) card.getCurrency());
        final BankCardHistory history = bankCardHistoryDAO.findList(new BankCardHistorySearchDTO().setCard(card)).getLast();
        assertEquals(BankCardHistoryType.DEPOSIT, history.getType());
        assertNotNull(history.getHistoryData());
        assertEquals("0 -> 100 (+100)", history.getHistoryData().get("valueChange").asText());
    }

    @Test
    void depositWrongCardTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber("1234 5677");
        request.setFunds(100L);
        request.setGameAccount("test-name");

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.DEPOSIT)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1401"));
    }

    @Test
    void depositWrongOwnerTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(100L);
        request.setGameAccount("test-name-1");

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.DEPOSIT)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1401"));
    }

    @Test
    void depositWrongFundsTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(0L);
        request.setGameAccount("test-name");

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.DEPOSIT)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1000"));
    }

    @Test
    void withdrawTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(100L);
        request.setPin("1234");
        request.setGameAccount("test-name");

        final BankCard card = bankCardDAO.findByNumber("1234 5678").get();
        card.setCurrency(100L);
        bankCardDAO.save(card);
        assertEquals(100, (long) card.getCurrency());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals("", result.getResponse().getContentAsString());
        assertEquals(0, (long) card.getCurrency());
        final BankCardHistory history = bankCardHistoryDAO.findList(new BankCardHistorySearchDTO().setCard(card)).getLast();
        assertEquals(BankCardHistoryType.WITHDRAW, history.getType());
        assertNotNull(history.getHistoryData());
        assertEquals("100 -> 0 (-100)", history.getHistoryData().get("valueChange").asText());
    }

    @Test
    void withdrawNotEnoughTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(100L);
        request.setPin("1234");
        request.setGameAccount("test-name");

        final BankCard card = bankCardDAO.findByNumber("1234 5678").get();
        assertEquals(0, (long) card.getCurrency());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1501"));
    }

    @Test
    void withdrawWrongCardTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber("1234 5677");
        request.setFunds(100L);
        request.setGameAccount("test-name");

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1401"));
    }

    @Test
    void withdrawWrongOwnerTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(100L);
        request.setPin("1234");
        request.setGameAccount("test-name-1");

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1401"));
    }

    @Test
    void withdrawWrongFundsTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(100L);
        request.setPin("1234");
        request.setGameAccount("test-name");

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1501"));
    }

    @Test
    void withdrawWrongPinTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(1L);
        request.setPin("1111");
        request.setGameAccount("test-name");

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1403"));
    }

    @Test
    void transferTest() throws Exception {
        final TransferFundsRequest request = new TransferFundsRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(100L);
        request.setPin("1234");
        request.setMessage("test");
        request.setReceiverCard("1234 5679");
        request.setGameAccount("test-name");

        final BankCard card = bankCardDAO.findByNumber("1234 5678").get();
        final BankCard cardReceiver = bankCardDAO.findByNumber("1234 5679").get();
        card.setCurrency(100L);
        bankCardDAO.save(card);
        assertEquals(100, (long) card.getCurrency());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.TRANSFER)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals("", result.getResponse().getContentAsString());
        assertEquals(0, (long) card.getCurrency());
        final BankCardHistory history = bankCardHistoryDAO.findList(new BankCardHistorySearchDTO().setCard(card)).getLast();
        final BankCardHistory historyReceiver = bankCardHistoryDAO.findList(new BankCardHistorySearchDTO().setCard(cardReceiver)).getLast();
        assertEquals(BankCardHistoryType.TRANSFER_FROM, history.getType());
        assertEquals(BankCardHistoryType.TRANSFER_TO, historyReceiver.getType());
        assertNotNull(history.getHistoryData());
        assertNotNull(historyReceiver.getHistoryData());
        assertEquals("100 -> 0 (-100)", history.getHistoryData().get("valueChange").asText());
        assertEquals("0 -> 100 (+100)", historyReceiver.getHistoryData().get("valueChange").asText());
        assertEquals("test", history.getHistoryData().get("description").asText());
        assertEquals("test", historyReceiver.getHistoryData().get("description").asText());
        assertEquals("1234 5679", history.getHistoryData().get("receiver").asText());
        assertEquals("1234 5678", historyReceiver.getHistoryData().get("sender").asText());
    }

    @Test
    void transferWrongPinTest() throws Exception {
        final TransferFundsRequest request = new TransferFundsRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(100L);
        request.setPin("1111");
        request.setMessage("test");
        request.setReceiverCard("1234 5679");
        request.setGameAccount("test-name");

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.TRANSFER)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1403"));
    }

    @Test
    void transferSameCardTest() throws Exception {
        final TransferFundsRequest request = new TransferFundsRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(100L);
        request.setPin("1234");
        request.setMessage("test");
        request.setReceiverCard("1234 5678");
        request.setGameAccount("test-name");

        final BankCard card = bankCardDAO.findByNumber("1234 5678").get();
        card.setCurrency(100L);
        bankCardDAO.save(card);
        assertEquals(100, (long) card.getCurrency());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.TRANSFER)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1410"));
    }
}