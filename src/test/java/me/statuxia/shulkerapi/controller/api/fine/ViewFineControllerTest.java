package me.statuxia.shulkerapi.controller.api.fine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.BankCardOperationHistoryDAO;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dao.impl.FineDAO;
import me.statuxia.shulkerapi.dao.impl.FineLogDAO;
import me.statuxia.shulkerapi.model.Fine;
import me.statuxia.shulkerapi.model.FineStatus;
import me.statuxia.shulkerapi.request.FineListRequest;
import me.statuxia.shulkerapi.response.NamedItem;
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
    "classpath:sql/ViewFineControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
class ViewFineControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";
    private static final String ADMIN_TOKEN = "admin-token-1";
    private static final String NOT_ADMIN_TOKEN_2 = "admin-token-2";

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

    @ParameterizedTest
    @CsvSource({"NEW,4", "PAYED,3", "OVERDUE,2", "CLOSED,1"})
    void list_statusFilter(FineStatus status, int expectedValue) throws Exception {
        final FineListRequest request = new FineListRequest();
        request.setStatuses(List.of(status));
        request.setGameAccount("test-name");

        mockMvc.perform(MockMvcRequestBuilders
                .post(ViewFineController.PREFIX + ViewFineController.LIST)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(String.valueOf(expectedValue)));
    }

    @ParameterizedTest
    @CsvSource({"NEW,4", "PAYED,3", "OVERDUE,2", "CLOSED,1"})
    void list_statusFilter_adminToken(FineStatus status, int expectedValue) throws Exception {
        final FineListRequest request = new FineListRequest();
        request.setStatuses(List.of(status));
        request.setGameAccount("test-name");

        mockMvc.perform(MockMvcRequestBuilders
                .post(ViewFineController.PREFIX + ViewFineController.LIST)
                .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(String.valueOf(expectedValue)));
    }


    @Test
    void list_statusFilter_noFilter() throws Exception {
        final FineListRequest request = new FineListRequest();
        request.setGameAccount("test-name");

        mockMvc.perform(MockMvcRequestBuilders
                .post(ViewFineController.PREFIX + ViewFineController.LIST)
                .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(String.valueOf(10)));
    }

    @Test
    void list_statusFilter_actionBy() throws Exception {
        final FineListRequest request = new FineListRequest();
        request.setGameAccount("test-name");
        request.setActionBy("admin-name-2");

        mockMvc.perform(MockMvcRequestBuilders
                .post(ViewFineController.PREFIX + ViewFineController.LIST)
                .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(String.valueOf(1)));
    }

    @Test
    void list_statusFilter_unnotifiedOnly() throws Exception {
        final FineListRequest request = new FineListRequest();
        request.setGameAccount("test-name");
        request.setUnnotifiedOnly(true);

        mockMvc.perform(MockMvcRequestBuilders
                .post(ViewFineController.PREFIX + ViewFineController.LIST)
                .header(X_TOKEN_HEADER, ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(String.valueOf(3)));

        assertTrue(fineDAO.findAll().stream().allMatch(Fine::isNotified));
    }

    @Test
    void list_statusFilter_notAdminToken() throws Exception {
        final FineListRequest request = new FineListRequest();
        request.setGameAccount("test-name");

        mockMvc.perform(MockMvcRequestBuilders
                .post(ViewFineController.PREFIX + ViewFineController.LIST)
                .header(X_TOKEN_HEADER, NOT_ADMIN_TOKEN_2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1201"));
    }

    @Test
    void statusTypes() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
            .get(ViewFineController.PREFIX + ViewFineController.STATUS_TYPES)
            .header(X_TOKEN_HEADER, SESSION_TOKEN)
            .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        final List<NamedItem> types = objectMapper.readValue(result.getResponse().getContentAsString(),
            new TypeReference<List<NamedItem>>() {
            }
        );
        assertEquals(List.of(
            new NamedItem("Новый", "NEW"),
            new NamedItem("Просрочен", "OVERDUE"),
            new NamedItem("Оплачен", "PAYED"),
            new NamedItem("Закрыт", "CLOSED")
        ), types);
    }

    @Test
    void actionTypes() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
            .get(ViewFineController.PREFIX + ViewFineController.ACTION_TYPES)
            .header(X_TOKEN_HEADER, SESSION_TOKEN)
            .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        final List<NamedItem> types = objectMapper.readValue(result.getResponse().getContentAsString(),
            new TypeReference<List<NamedItem>>() {
            }
        );

        assertEquals(List.of(
            new NamedItem("Изменение сообщения", "EDIT_MESSAGE"),
            new NamedItem("Закрытие штрафа", "CLOSE")
        ), types);
    }
}