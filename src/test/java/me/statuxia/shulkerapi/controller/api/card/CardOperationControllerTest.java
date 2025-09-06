package me.statuxia.shulkerapi.controller.api.card;

import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.model.BankCard;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
    "classpath:sql/CardControllerTest.sql",
    "classpath:sql/CardOperationControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
class CardOperationControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";

    @Autowired
    protected BankCardDAO bankCardDAO;

    @Autowired
    protected MockMvc mockMvc;

    @ParameterizedTest
    @CsvSource({"1, -100", "3, 100"})
    void testRollback(Long id, Long expectedCurrency) throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.post(CardOperationController.PREFIX + CardOperationController.ROLLBACK + "/%s".formatted(id))
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        final BankCard card = bankCardDAO.findById(1L).get();
        assertEquals(expectedCurrency, card.getCurrency());
    }

    @ParameterizedTest
    @CsvSource({"2", "4"})
    void testWrongRollback(Long id) throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post(CardOperationController.PREFIX + CardOperationController.ROLLBACK + "/%s".formatted(id))
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1603"));
    }

    @ParameterizedTest
    @CsvSource({"2, 100", "4, -100"})
    void testRestore(Long id, Long expectedCurrency) throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.post(CardOperationController.PREFIX + CardOperationController.RESTORE + "/%s".formatted(id))
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        final BankCard card = bankCardDAO.findById(1L).get();
        assertEquals(expectedCurrency, card.getCurrency());
    }

    @ParameterizedTest
    @CsvSource({"1", "3"})
    void testWrongRestore(Long id) throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post(CardOperationController.PREFIX + CardOperationController.RESTORE + "/%s".formatted(id))
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1603"));
    }
}