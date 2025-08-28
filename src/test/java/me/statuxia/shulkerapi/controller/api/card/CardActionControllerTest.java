package me.statuxia.shulkerapi.controller.api.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.request.ChangeCardBalanceRequest;
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
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void depositTest() throws Exception {
        final ChangeCardBalanceRequest request = new ChangeCardBalanceRequest();
        request.setCardNumber("1234 5678");
        request.setFunds(100L);
        request.setGameAccount("test-name");

        assertEquals(0, (long) bankCardDAO.findByNumber("1234 5678").get().getCurrency());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.DEPOSIT)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals("", result.getResponse().getContentAsString());
        assertEquals(100, (long) bankCardDAO.findByNumber("1234 5678").get().getCurrency());
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
        assertEquals(0, (long) bankCardDAO.findByNumber("1234 5678").get().getCurrency());
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
        request.setFunds(0L);
        request.setPin("1111");
        request.setGameAccount("test-name");

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(CardController.PREFIX + CardActionController.WITHDRAW)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("1000"));
    }

}