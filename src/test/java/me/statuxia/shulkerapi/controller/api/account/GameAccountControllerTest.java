package me.statuxia.shulkerapi.controller.api.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.request.GameAccountCreateRequest;
import me.statuxia.shulkerapi.response.GameAccountResponse;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
    "classpath:sql/GameAccountControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
class GameAccountControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";

    @Autowired
    protected GameAccountDAO gameAccountDAO;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    void newNameTest() throws Exception {
        final GameAccountCreateRequest request = new GameAccountCreateRequest();
        request.setName("new-name");
        request.setDiscordId(1L);

        final GameAccountResponse response = new GameAccountResponse(100000002L, "new-name", 1L);

        assertTrue(gameAccountDAO.findByName("new-name").isEmpty());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(GameAccountController.PREFIX + GameAccountController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(response), result.getResponse().getContentAsString());
        assertTrue(gameAccountDAO.findByName("new-name").isPresent());
    }

    @Test
    void changeNameTest() throws Exception {
        final GameAccountCreateRequest request = new GameAccountCreateRequest();
        request.setName("Test-Name");
        request.setDiscordId(1L);

        final GameAccountResponse response = new GameAccountResponse(1L, "Test-Name", 1L);

        assertTrue(gameAccountDAO.findByName("test-name").isPresent());
        assertTrue(gameAccountDAO.findByName("Test-Name").isEmpty());
        assertFalse(gameAccountDAO.findByNameIgnoreCase("Test-Name").isEmpty());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(GameAccountController.PREFIX + GameAccountController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(response), result.getResponse().getContentAsString());

        assertTrue(gameAccountDAO.findByName("Test-Name").isPresent());
        assertTrue(gameAccountDAO.findByName("test-name").isEmpty());
        assertFalse(gameAccountDAO.findByNameIgnoreCase("Test-Name").isEmpty());
        assertFalse(gameAccountDAO.findByNameIgnoreCase("Test-Name").getFirst().isPaid());
    }

    @Test
    void newTwinkTest() throws Exception {
        final GameAccountCreateRequest request = new GameAccountCreateRequest();
        request.setName("test-paid-account");
        request.setDiscordId(1L);
        request.setTwink(true);

        final GameAccountResponse response = new GameAccountResponse(100000002L, "test-paid-account", 1L);

        assertTrue(gameAccountDAO.findByName("test-paid-account").isEmpty());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(GameAccountController.PREFIX + GameAccountController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(response), result.getResponse().getContentAsString());
    }

    @Test
    void newTwinkTest_noLinkedAccounts() throws Exception {
        final GameAccountCreateRequest request = new GameAccountCreateRequest();
        request.setName("Test-Name");
        request.setDiscordId(2L);
        request.setTwink(true);

        mockMvc.perform(
                MockMvcRequestBuilders.post(GameAccountController.PREFIX + GameAccountController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1210"));
    }

    @Test
    void newTwinkTest_alreadyLinkedAccount() throws Exception {
        final GameAccountCreateRequest request = new GameAccountCreateRequest();
        request.setName("Test-Name");
        request.setDiscordId(1L);
        request.setTwink(false);

        mockMvc.perform(
                MockMvcRequestBuilders.post(GameAccountController.PREFIX + GameAccountController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk());

        request.setDiscordId(2L);

        mockMvc.perform(
                MockMvcRequestBuilders.post(GameAccountController.PREFIX + GameAccountController.CREATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1204"));
    }
}