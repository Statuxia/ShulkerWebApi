package me.statuxia.shulkerapi.controller.api.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardHistorySearchDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import me.statuxia.shulkerapi.model.CardStyleType;
import me.statuxia.shulkerapi.request.CardStyleRequest;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class CardStyleControllerTest extends BaseContainerTest {

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
    void changeStyleTest() throws Exception {
        final CardStyleRequest request = new CardStyleRequest();
        request.setGameAccount("test-name");
        request.setNewStyle(CardStyleType.SUNSET);
        request.setPin("1234");
        request.setCardNumber("1234 5678");

        final BankCard cardBeforeChanges = bankCardDAO.findByNumber(request.getCardNumber()).get();
        cardBeforeChanges.setCurrency(10000L);
        bankCardDAO.save(cardBeforeChanges);
        assertEquals(0, cardBeforeChanges.getPatternSeed());
        assertEquals(CardStyleType.DEFAULT, cardBeforeChanges.getCardStyle());

        mockMvc.perform(
            MockMvcRequestBuilders.put(CardController.PREFIX + CardStyleController.CHANGE_STYLE)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        final BankCard card = bankCardDAO.findByNumber(request.getCardNumber()).get();

        assertTrue(card.getPatternSeed() >= 0 && card.getPatternSeed() <= 1000);
        assertEquals(CardStyleType.SUNSET, card.getCardStyle());
        assertEquals(
            BankCardHistoryType.CHANGE_STYLE,
            bankCardHistoryDAO.findList(new BankCardHistorySearchDTO().setCard(card)).getLast().getType()
        );
    }
}