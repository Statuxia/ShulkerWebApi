package me.statuxia.shulkerapi.controller.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dao.impl.GameSessionIpDAO;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.GameSessionIp;
import me.statuxia.shulkerapi.model.GameSessionIpState;
import me.statuxia.shulkerapi.request.*;
import me.statuxia.shulkerapi.response.AuthRefreshResponse;
import me.statuxia.shulkerapi.response.AuthRefreshResponseItem;
import me.statuxia.shulkerapi.response.AuthValidateResponse;
import me.statuxia.shulkerapi.response.GameSessionIpResponse;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
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
import java.util.Optional;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Sql({
    "classpath:sql/AuthControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@Transactional
@TestPropertySource("classpath:test-application.properties")
@DirtiesContext
class AuthControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected GameSessionIpDAO gameSessionIpDAO;

    @Autowired
    protected GameAccountDAO gameAccountDAO;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        gameSessionIpDAO.getEntityManager()
            .createNativeQuery("TRUNCATE TABLE game_session_ip CASCADE")
            .executeUpdate();
        gameSessionIpDAO.getEntityManager()
            .createNativeQuery("ALTER SEQUENCE game_session_ip_id_seq RESTART WITH 1")
            .executeUpdate();
    }

    @Test
    void validateNotGameAccountTest() throws Exception {
        final AuthValidateRequest request = new AuthValidateRequest().setName("test-name-x").setIp("0.0.0.0");

        assertNull(cacheManager.getCache("auth_validate").get("test-name-x_0.0.0.0"));

        mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.VALIDATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1201"));

        assertEquals(0, gameSessionIpDAO.countAll());

        assertNull(cacheManager.getCache("auth_validate").get("test-name-x_0.0.0.0"));
    }

    @Test
    void validateNewStartedTest() throws Exception {
        final AuthValidateRequest request = new AuthValidateRequest().setName("test-name").setIp("0.0.0.0");

        assertTrue(gameSessionIpDAO.findById(1L).isEmpty());
        assertNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.VALIDATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(
            new AuthValidateResponse().setState(GameSessionIpState.STARTED),
            objectMapper.readValue(result.getResponse().getContentAsString(), AuthValidateResponse.class)
        );
        assertEquals(1, gameSessionIpDAO.countAll());
        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertEquals(GameSessionIpState.STARTED, gameSessionIpDAO.findById(1L).get().getState());

        assertNotNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));
        cacheManager.getCache("auth_validate").evict("test-name_0.0.0.0");
        assertNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));
    }

    @Test
    void validateAlreadyStartedTest() throws Exception {
        final AuthValidateRequest request = new AuthValidateRequest().setName("test-name").setIp("0.0.0.0");

        assertTrue(gameSessionIpDAO.findById(1L).isEmpty());
        assertNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));

        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(GameSessionIpState.STARTED);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertEquals(sessionIp.getId(), gameSessionIpDAO.findById(1L).get().getId());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.VALIDATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(
            new AuthValidateResponse().setState(GameSessionIpState.STARTED),
            objectMapper.readValue(result.getResponse().getContentAsString(), AuthValidateResponse.class)
        );
        assertEquals(1, gameSessionIpDAO.countAll());
        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertEquals(GameSessionIpState.STARTED, gameSessionIpDAO.findById(1L).get().getState());

        assertNotNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));
        cacheManager.getCache("auth_validate").evict("test-name_0.0.0.0");
        assertNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));
    }

    @Test
    void validateNotNotifiedTest() throws Exception {
        final AuthValidateRequest request = new AuthValidateRequest().setName("test-name").setIp("0.0.0.0");

        assertTrue(gameSessionIpDAO.findById(1L).isEmpty());
        assertNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));

        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(GameSessionIpState.NOT_NOTIFIED);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertEquals(sessionIp.getId(), gameSessionIpDAO.findById(1L).get().getId());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.VALIDATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(
            new AuthValidateResponse().setState(GameSessionIpState.NOT_NOTIFIED),
            objectMapper.readValue(result.getResponse().getContentAsString(), AuthValidateResponse.class)
        );
        assertEquals(2, gameSessionIpDAO.countAll());
        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertTrue(gameSessionIpDAO.findById(2L).isPresent());
        assertEquals(GameSessionIpState.NOT_NOTIFIED, gameSessionIpDAO.findById(1L).get().getState());
        assertEquals(GameSessionIpState.STARTED, gameSessionIpDAO.findById(2L).get().getState());

        assertNotNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));
        cacheManager.getCache("auth_validate").evict("test-name_0.0.0.0");
        assertNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));
    }

    @ParameterizedTest
    @CsvSource({"true,0", "false,1"})
    void queueNotNotifiedTest(boolean notified, int total) throws Exception {
        assertTrue(gameSessionIpDAO.findById(1L).isEmpty());

        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(notified);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(GameSessionIpState.STARTED);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertEquals(sessionIp.getId(), gameSessionIpDAO.findById(1L).get().getId());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.QUEUE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new PaginationRequest()))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(
            total,
            objectMapper.readValue(result.getResponse().getContentAsString(), GameSessionIpResponse.class).getTotal()
        );
    }

    @Test
    void validateOutdatedTest() throws Exception {
        final AuthValidateRequest request = new AuthValidateRequest().setName("test-name").setIp("0.0.0.0");

        assertTrue(gameSessionIpDAO.findById(1L).isEmpty());
        assertNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));

        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(GameSessionIpState.OUTDATED);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertEquals(sessionIp.getId(), gameSessionIpDAO.findById(1L).get().getId());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.VALIDATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(
            new AuthValidateResponse().setState(GameSessionIpState.STARTED),
            objectMapper.readValue(result.getResponse().getContentAsString(), AuthValidateResponse.class)
        );
        assertEquals(2, gameSessionIpDAO.countAll());
        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertTrue(gameSessionIpDAO.findById(2L).isPresent());
        assertEquals(GameSessionIpState.OUTDATED, gameSessionIpDAO.findById(1L).get().getState());
        assertEquals(GameSessionIpState.STARTED, gameSessionIpDAO.findById(2L).get().getState());

        assertNotNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));
        cacheManager.getCache("auth_validate").evict("test-name_0.0.0.0");
        assertNull(cacheManager.getCache("auth_validate").get("test-name_0.0.0.0"));
    }

    @Test
    void changeUnknownStateTest() throws Exception {
        final AuthChangeStateRequest request = new AuthChangeStateRequest()
            .setId(1L);

        assertTrue(gameSessionIpDAO.findById(1L).isEmpty());

        mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.CHANGE_STATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("2001"));
    }

    @Test
    void changeOnlyNotifyStateTest() throws Exception {
        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(GameSessionIpState.STARTED);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        final AuthChangeStateRequest request = new AuthChangeStateRequest().setId(sessionIp.getId());

        mockMvc.perform(
            MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.CHANGE_STATE)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(GameSessionIpState.STARTED, sessionIp.getState());
        assertTrue(sessionIp.isNotified());
    }

    @ParameterizedTest
    @EnumSource(value = GameSessionIpState.class,
        names = {"STARTED", "OUTDATED"})
    void changeBadRequestTest(GameSessionIpState state) throws Exception {
        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(GameSessionIpState.STARTED);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        final AuthChangeStateRequest request = new AuthChangeStateRequest().setId(sessionIp.getId()).setState(state);

        mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.CHANGE_STATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("2002"));
    }

    @ParameterizedTest
    @EnumSource(value = GameSessionIpState.class,
        names = {"NOT_NOTIFIED", "REJECTED", "OUTDATED"})
    void changeBadCurrentStateTest(GameSessionIpState state) throws Exception {
        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(state);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        final AuthChangeStateRequest request = new AuthChangeStateRequest().setId(sessionIp.getId())
            .setState(GameSessionIpState.REJECTED);

        mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.CHANGE_STATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("2003"));
    }

    @ParameterizedTest
    @EnumSource(value = GameSessionIpState.class,
        names = {"ACCEPTED", "STARTED"})
    void changeStateTest(GameSessionIpState state) throws Exception {
        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(state);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        final AuthChangeStateRequest request = new AuthChangeStateRequest().setId(sessionIp.getId())
            .setState(GameSessionIpState.REJECTED);

        mockMvc.perform(
            MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.CHANGE_STATE)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(GameSessionIpState.REJECTED, sessionIp.getState());
        assertTrue(sessionIp.isNotified());
    }

    @Test
    void changeStateRejectedTest() throws Exception {
        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(GameSessionIpState.REJECTED);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        final AuthChangeStateRequest request = new AuthChangeStateRequest().setId(sessionIp.getId())
            .setState(GameSessionIpState.ACCEPTED);

        mockMvc.perform(
            MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.CHANGE_STATE)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(GameSessionIpState.ACCEPTED, sessionIp.getState());
        assertTrue(sessionIp.isNotified());
    }

    @Test
    void refreshNoAccountTest() throws Exception {
        final AuthRefreshRequestItem requestItem = new AuthRefreshRequestItem().setIp("0.0.0.0").setName("test-name-x");
        final AuthRefreshRequest request = new AuthRefreshRequest().setItems(List.of(requestItem));

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.REFRESH)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk()).andReturn();


        final AuthRefreshResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AuthRefreshResponse.class
        );

        final AuthRefreshResponseItem item = response.getItems().getFirst();
        assertEquals("test-name-x", item.getUsername());
        assertFalse(item.isSuccess());
        assertEquals("Неизвестный аккаунт", item.getErrorMessage());
        assertEquals(1201, item.getErrorCode());
    }

    @Test
    void refreshUnknownSessionCauseOfIpTest() throws Exception {
        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(GameSessionIpState.ACCEPTED);
        sessionIp.setIp("0.0.0.1");
        gameSessionIpDAO.save(sessionIp);

        final AuthRefreshRequestItem requestItem = new AuthRefreshRequestItem().setIp("0.0.0.0").setName("test-name");
        final AuthRefreshRequest request = new AuthRefreshRequest().setItems(List.of(requestItem));

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.REFRESH)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk()).andReturn();

        final AuthRefreshResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AuthRefreshResponse.class
        );

        final AuthRefreshResponseItem item = response.getItems().getFirst();
        assertEquals("test-name", item.getUsername());
        assertFalse(item.isSuccess());
        assertEquals("Неизвестная игровая сессия", item.getErrorMessage());
        assertEquals(2001, item.getErrorCode());
    }


    @Test
    void refreshUnknownSessionCauseOfStateTest() throws Exception {
        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(GameSessionIpState.STARTED);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        final AuthRefreshRequestItem requestItem = new AuthRefreshRequestItem().setIp("0.0.0.0").setName("test-name");
        final AuthRefreshRequest request = new AuthRefreshRequest().setItems(List.of(requestItem));

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.REFRESH)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk()).andReturn();

        final AuthRefreshResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AuthRefreshResponse.class
        );

        final AuthRefreshResponseItem item = response.getItems().getFirst();
        assertEquals("test-name", item.getUsername());
        assertFalse(item.isSuccess());
        assertEquals("Неизвестная игровая сессия", item.getErrorMessage());
        assertEquals(2001, item.getErrorCode());
    }

    @Test
    void refreshUnknownSessionCauseOfOutdatedTest() throws Exception {
        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().minusDays(1));
        sessionIp.setState(GameSessionIpState.ACCEPTED);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        final AuthRefreshRequestItem requestItem = new AuthRefreshRequestItem().setIp("0.0.0.0").setName("test-name");
        final AuthRefreshRequest request = new AuthRefreshRequest().setItems(List.of(requestItem));

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.REFRESH)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        final AuthRefreshResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AuthRefreshResponse.class
        );

        final AuthRefreshResponseItem item = response.getItems().getFirst();
        assertEquals("test-name", item.getUsername());
        assertFalse(item.isSuccess());
        assertEquals("Неизвестная игровая сессия", item.getErrorMessage());
        assertEquals(2001, item.getErrorCode());
    }

    @Test
    void refreshTest() throws Exception {
        final Optional<GameAccount> optGameAccount = gameAccountDAO.findByName("test-name");
        final GameSessionIp sessionIp = new GameSessionIp();
        sessionIp.setGameAccount(optGameAccount.get());
        sessionIp.setNotified(false);
        sessionIp.setLastJoinDate(DateTime.now().plusDays(10));
        sessionIp.setState(GameSessionIpState.ACCEPTED);
        sessionIp.setIp("0.0.0.0");
        gameSessionIpDAO.save(sessionIp);

        final AuthRefreshRequestItem requestItem = new AuthRefreshRequestItem().setIp("0.0.0.0").setName("test-name");
        final AuthRefreshRequest request = new AuthRefreshRequest().setItems(List.of(requestItem));

        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.REFRESH)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk()).andReturn();

        final AuthRefreshResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AuthRefreshResponse.class
        );

        final AuthRefreshResponseItem item = response.getItems().getFirst();
        assertEquals("test-name", item.getUsername());
        assertTrue(item.isSuccess());
        assertNull(item.getErrorMessage());
        assertNull(item.getErrorCode());
    }
}