package me.statuxia.shulkerapi.controller.api.fine;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.BankCardOperationHistoryDAO;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dao.impl.FineDAO;
import me.statuxia.shulkerapi.dao.impl.FineLogDAO;
import me.statuxia.shulkerapi.dto.search.impl.FineSearchDTO;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.request.CloseFineRequest;
import me.statuxia.shulkerapi.request.CreateFineRequest;
import me.statuxia.shulkerapi.request.EditMessageFineRequest;
import me.statuxia.shulkerapi.request.PayFineRequest;
import me.statuxia.shulkerapi.utils.DateUtils;
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

import java.util.List;
import java.util.Optional;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
    "classpath:sql/FineManagerControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
class FineManagementControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";
    private static final String ADMIN_TOKEN = "admin-token-1";

    @Autowired
    protected BankCardHistoryDAO bankCardHistory;

    @Autowired
    protected BankCardOperationHistoryDAO bankCardOperationDAO;

    @Autowired
    protected FineDAO fineDAO;

    @Autowired
    protected FineLogDAO fineLogDAO;

    @Autowired
    protected GameAccountDAO gameAccountDAO;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    // <editor-fold defaultstate="collapsed" desc="createTest">
    @Test
    void createTest_success() throws Exception {
        final CreateFineRequest request = new CreateFineRequest()
            .setGameAccount("test-name")
            .setValue(500L)
            .setDueDate(DateUtils.DATETIME.parseDateTime("01.01.2125 00:00:00"))
            .setMessage("testMessage")
            .setActionBy("admin-name-1");

        assertEquals(0, fineDAO.countAll());

        mockMvc.perform(
            MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.CREATE)
                .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(1, fineDAO.countAll());
        Optional<Fine> fine = fineDAO.find(new FineSearchDTO().setGameAccount(gameAccountDAO.findById(1L).get()));
        assertTrue(fine.isPresent());

        assertEquals("testMessage", fine.get().getMessage());
        assertEquals(500, fine.get().getFineValue());
        assertEquals(FineStatus.NEW, fine.get().getStatus());
        assertEquals("admin-name-1", fine.get().getActionBy());
        assertEquals(gameAccountDAO.findByName("test-name").get(), fine.get().getGameAccount());
        assertEquals(DateUtils.DATETIME.parseDateTime("01.01.2125 00:00:00"), fine.get().getDueDate());
    }

    @Test
    void createTest_unknownGameAccount() throws Exception {
        final CreateFineRequest request = new CreateFineRequest()
            .setGameAccount("unknown")
            .setValue(500L)
            .setDueDate(DateUtils.DATETIME.parseDateTime("01.01.2125 00:00:00"))
            .setMessage("testMessage")
            .setActionBy("admin-name-1");

        assertEquals(0, fineDAO.countAll());

        mockMvc.perform(
                MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.CREATE)
                    .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1201"));

        assertEquals(0, fineDAO.countAll());
    }

    @Test
    void createTest_unknownActionByAccount() throws Exception {
        final CreateFineRequest request = new CreateFineRequest()
            .setGameAccount("test-name")
            .setValue(500L)
            .setDueDate(DateUtils.DATETIME.parseDateTime("01.01.2125 00:00:00"))
            .setMessage("testMessage")
            .setActionBy("unknown");

        assertEquals(0, fineDAO.countAll());

        mockMvc.perform(
                MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.CREATE)
                    .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1202"));

        assertEquals(0, fineDAO.countAll());
    }

    @Test
    void createTest_dueDateInPast() throws Exception {
        final CreateFineRequest request = new CreateFineRequest()
            .setGameAccount("test-name")
            .setValue(500L)
            .setDueDate(DateUtils.DATETIME.parseDateTime("01.01.2025 00:00:00"))
            .setMessage("testMessage")
            .setActionBy("admin-name-1");

        assertEquals(0, fineDAO.countAll());

        mockMvc.perform(
                MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.CREATE)
                    .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1903"));

        assertEquals(0, fineDAO.countAll());
    }

    @Test
    void createTest_noAuthority() throws Exception {
        final CreateFineRequest request = new CreateFineRequest()
            .setGameAccount("test-name")
            .setValue(500L)
            .setDueDate(DateUtils.DATETIME.parseDateTime("01.01.2025 00:00:00"))
            .setMessage("testMessage")
            .setActionBy("admin-name-1");

        assertEquals(0, fineDAO.countAll());

        mockMvc.perform(
                MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized());

        assertEquals(0, fineDAO.countAll());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="editMessage">
    @Sql({
        "classpath:sql/FineManagerControllerTest.sql",
        "classpath:sql/FineManagerControllerTest_SomeFines.sql"
    })
    @Test
    void editMessageTest_success() throws Exception {
        final EditMessageFineRequest request = new EditMessageFineRequest()
            .setMessage("testMessage");
        request.setActionBy("admin-name-1");
        request.setId(1L);

        assertEquals(4, fineDAO.countAll());
        assertEquals("hello world", fineDAO.findById(1L).get().getMessage());

        mockMvc.perform(
                MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.EDIT_MESSAGE)
                    .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk());

        assertEquals(4, fineDAO.countAll());
        assertEquals("testMessage", fineDAO.findById(1L).get().getMessage());

        final FineLog log = fineLogDAO.findAll().getFirst();
        assertEquals(FineAction.EDIT_MESSAGE, log.getAction());
        assertEquals("admin-name-1", log.getActionBy());


        assertEquals("hello world", log.getData().get("oldValue").asText());
        assertEquals("testMessage", log.getData().get("newValue").asText());
    }

    @Sql({
        "classpath:sql/FineManagerControllerTest.sql",
        "classpath:sql/FineManagerControllerTest_SomeFines.sql"
    })
    @Test
    void editMessageTest_statusFinal() throws Exception {
        for (Long i : List.of(2L, 4L)) {
            final EditMessageFineRequest request = new EditMessageFineRequest()
                .setMessage("testMessage");
            request.setActionBy("admin-name-1");
            request.setId(i);

            assertEquals(4, fineDAO.countAll());

            mockMvc.perform(
                    MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.EDIT_MESSAGE)
                        .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("1902"));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="closeTest">
    @Sql({
        "classpath:sql/FineManagerControllerTest.sql",
        "classpath:sql/FineManagerControllerTest_SomeFines.sql"
    })
    @Test
    void closeTest_success() throws Exception {
        final CloseFineRequest request = new CloseFineRequest();
        request.setActionBy("admin-name-1");
        request.setId(1L);

        assertEquals(4, fineDAO.countAll());
        assertEquals(FineStatus.NEW, fineDAO.findById(1L).get().getStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.CLOSE)
                    .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk());

        assertEquals(4, fineDAO.countAll());
        assertEquals(FineStatus.CLOSED, fineDAO.findById(1L).get().getStatus());

        final FineLog log = fineLogDAO.findAll().getFirst();
        assertEquals(FineAction.CLOSE, log.getAction());
        assertEquals("admin-name-1", log.getActionBy());

        assertEquals("enum.FineStatus.NEW", log.getData().get("oldValue").asText());
        assertEquals("enum.FineStatus.CLOSED", log.getData().get("newValue").asText());
    }


    @Sql({
        "classpath:sql/FineManagerControllerTest.sql",
        "classpath:sql/FineManagerControllerTest_SomeFines.sql"
    })
    @Test
    void closeTest_statusFinal() throws Exception {
        for (Long i : List.of(4L)) {
            final EditMessageFineRequest request = new EditMessageFineRequest()
                .setMessage("testMessage");
            request.setActionBy("admin-name-1");
            request.setId(i);

            assertEquals(4, fineDAO.countAll());

            mockMvc.perform(
                    MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.CLOSE)
                        .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("1902"));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="payTest">
    @Sql({
        "classpath:sql/FineManagerControllerTest.sql",
        "classpath:sql/FineManagerControllerTest_SomeFines.sql"
    })
    @Test
    void payTest_success() throws Exception {
        final PayFineRequest request = new PayFineRequest()
            .setId(1L)
            .setGameAccount("test-name")
            .setPaymentCardNumber("1234 5678")
            .setPaymentCardPin("1234");

        assertEquals(4, fineDAO.countAll());
        assertEquals(100, fineDAO.findById(1L).get().getFineValue());

        mockMvc.perform(
            MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.PAY)
                .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(4, fineDAO.countAll());
        final Optional<Fine> fine = fineDAO.findById(1L);
        assertTrue(fine.isPresent());

        assertEquals("hello world", fine.get().getMessage());
        assertEquals(100, fine.get().getFineValue());
        assertEquals(FineStatus.PAYED, fine.get().getStatus());
        assertEquals("admin-name-1", fine.get().getActionBy());
        assertEquals(gameAccountDAO.findByName("test-name").get(), fine.get().getGameAccount());

        final BankCardHistory history = bankCardHistory.findAll().getFirst();
        assertEquals(BankCardHistoryType.PAY_FINE, history.getType());

        final BankCardOperationHistory operation = bankCardOperationDAO.findAll().getFirst();
        assertEquals(-100L, operation.getValue());
    }

    @Sql({
        "classpath:sql/FineManagerControllerTest.sql",
        "classpath:sql/FineManagerControllerTest_SomeFines.sql"
    })
    @Test
    void payTest_successClient() throws Exception {
        final PayFineRequest request = new PayFineRequest()
            .setId(1L)
            .setGameAccount("test-name")
            .setPaymentCardNumber("1234 5678")
            .setPaymentCardPin("1234");

        assertEquals(4, fineDAO.countAll());
        assertEquals(100, fineDAO.findById(1L).get().getFineValue());

        mockMvc.perform(
            MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.PAY)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(4, fineDAO.countAll());
        final Optional<Fine> fine = fineDAO.findById(1L);
        assertTrue(fine.isPresent());

        assertEquals("hello world", fine.get().getMessage());
        assertEquals(100, fine.get().getFineValue());
        assertEquals(FineStatus.PAYED, fine.get().getStatus());
        assertEquals("admin-name-1", fine.get().getActionBy());
        assertEquals(gameAccountDAO.findByName("test-name").get(), fine.get().getGameAccount());

        final BankCardHistory history = bankCardHistory.findAll().getFirst();
        assertEquals(BankCardHistoryType.PAY_FINE, history.getType());

        final BankCardOperationHistory operation = bankCardOperationDAO.findAll().getFirst();
        assertEquals(-100L, operation.getValue());
    }

    @Sql({
        "classpath:sql/FineManagerControllerTest.sql",
        "classpath:sql/FineManagerControllerTest_SomeFines.sql"
    })
    @Test
    void payTest_wrongClient() throws Exception {
        final PayFineRequest request = new PayFineRequest()
            .setId(1L)
            .setGameAccount("admin-name-1")
            .setPaymentCardNumber("1111 1111")
            .setPaymentCardPin("1234");

        assertEquals(4, fineDAO.countAll());
        assertEquals(100, fineDAO.findById(1L).get().getFineValue());

        mockMvc.perform(
                MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.PAY)
                    .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1901"));
    }

    @Sql({
        "classpath:sql/FineManagerControllerTest.sql",
        "classpath:sql/FineManagerControllerTest_SomeFines.sql"
    })
    @Test
    void payTest_strangerCard() throws Exception {
        final PayFineRequest request = new PayFineRequest()
            .setId(1L)
            .setGameAccount("admin-name-1")
            .setPaymentCardNumber("1111 1111")
            .setPaymentCardPin("1234");

        assertEquals(4, fineDAO.countAll());
        assertEquals(100, fineDAO.findById(1L).get().getFineValue());

        mockMvc.perform(
                MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.PAY)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1201"));
    }

    @Sql({
        "classpath:sql/FineManagerControllerTest.sql",
        "classpath:sql/FineManagerControllerTest_SomeFines.sql"
    })
    @Test
    void payTest_statusFinal() throws Exception {
        final PayFineRequest request = new PayFineRequest()
            .setId(2L)
            .setGameAccount("test-name")
            .setPaymentCardNumber("1111 1111")
            .setPaymentCardPin("1234");

        assertEquals(4, fineDAO.countAll());
        assertEquals(100, fineDAO.findById(2L).get().getFineValue());

        mockMvc.perform(
                MockMvcRequestBuilders.post(FineManagementController.PREFIX + FineManagementController.PAY)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1902"));
    }
    // </editor-fold>
}