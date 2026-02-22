package me.statuxia.shulkerapi.controller.api.accountrole;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.request.AccountRoleRequest;
import me.statuxia.shulkerapi.response.AccountRoleResponseItem;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
    "classpath:sql/AccountRoleControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
class AccountRoleControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getForBatch_success() throws Exception {
        AccountRoleRequest request = new AccountRoleRequest();
        request.setDiscordIds(List.of(1L));

        MvcResult result = mockMvc.perform(
                post(AccountRoleController.PREFIX + AccountRoleController.GET_FOR_BATCH_PATH)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        List<AccountRoleResponseItem> response =
            objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {}
            );

        assertEquals(1, response.size());

        AccountRoleResponseItem item = response.getFirst();
        assertEquals(1L, item.getId());
        assertFalse(item.getRoles().isEmpty());
    }

    @Test
    void getForBatch_multipleDiscordIds_success() throws Exception {
        AccountRoleRequest request = new AccountRoleRequest();
        request.setDiscordIds(List.of(1L, 999L));

        MvcResult result = mockMvc.perform(
                post(AccountRoleController.PREFIX + AccountRoleController.GET_FOR_BATCH_PATH)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andReturn();

        List<AccountRoleResponseItem> response =
            objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {}
            );

        assertEquals(2, response.size());

        AccountRoleResponseItem item1 = response.stream()
            .filter(i -> i.getId().equals(1L))
            .findFirst()
            .orElseThrow();
        assertFalse(item1.getRoles().isEmpty());

        AccountRoleResponseItem item999 = response.stream()
            .filter(i -> i.getId().equals(999L))
            .findFirst()
            .orElseThrow();
        assertTrue(item999.getRoles().isEmpty());
    }

    @Test
    void getForBatch_emptyDiscordIds_returnsEmptyList() throws Exception {
        AccountRoleRequest request = new AccountRoleRequest();
        request.setDiscordIds(List.of());

        MvcResult result = mockMvc.perform(
                post(AccountRoleController.PREFIX + AccountRoleController.GET_FOR_BATCH_PATH)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        List<AccountRoleResponseItem> response =
            objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertTrue(response.isEmpty());
    }

    @Test
    void getForBatch_noToken_returnsUnauthorized() throws Exception {
        AccountRoleRequest request = new AccountRoleRequest();
        request.setDiscordIds(List.of(1L));

        mockMvc.perform(
            post(AccountRoleController.PREFIX + AccountRoleController.GET_FOR_BATCH_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void getForBatch_duplicateDiscordIds_returnsSingleRoleForId() throws Exception {
        AccountRoleRequest request = new AccountRoleRequest();
        request.setDiscordIds(List.of(1L, 1L));

        MvcResult result = mockMvc.perform(
                post(AccountRoleController.PREFIX + AccountRoleController.GET_FOR_BATCH_PATH)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andReturn();

        List<AccountRoleResponseItem> response =
            objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(1, response.size());

        assertEquals(1L, response.getFirst().getId());
    }
}