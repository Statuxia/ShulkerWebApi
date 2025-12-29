package me.statuxia.shulkerapi.controller.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dao.impl.GameSessionIpDAO;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.GameSessionIp;
import me.statuxia.shulkerapi.model.GameSessionIpState;
import me.statuxia.shulkerapi.request.*;
import me.statuxia.shulkerapi.response.AuthValidateResponse;
import me.statuxia.shulkerapi.response.AuthValidateResponseItem;
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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        final AuthValidateRequestItem requestItem = new AuthValidateRequestItem()
            .setName("test-name-x").setIp("0.0.0.0");
        final AuthValidateRequest request = new AuthValidateRequest().setItems(List.of(requestItem));

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.VALIDATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andReturn();

        final AuthValidateResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AuthValidateResponse.class
        );

        assertTrue(CollectionUtils.isEmpty(response.getItems()));
    }

    @Test
    void validateNewStartedTest() throws Exception {
        final AuthValidateRequestItem requestItem = new AuthValidateRequestItem().setName("test-name").setIp("0.0.0.0");
        final AuthValidateRequest request = new AuthValidateRequest().setItems(List.of(requestItem));

        assertTrue(gameSessionIpDAO.findById(1L).isEmpty());

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.VALIDATE)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(
            new AuthValidateResponseItem().setState(GameSessionIpState.STARTED),
            objectMapper.readValue(result.getResponse().getContentAsString(), AuthValidateResponse.class)
                .getItems().getFirst()
        );
        assertEquals(1, gameSessionIpDAO.countAll());
        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertEquals(GameSessionIpState.STARTED, gameSessionIpDAO.findById(1L).get().getState());
    }

    @Test
    void validateAlreadyStartedTest() throws Exception {
        final AuthValidateRequestItem requestItem = new AuthValidateRequestItem().setName("test-name").setIp("0.0.0.0");
        final AuthValidateRequest request = new AuthValidateRequest().setItems(List.of(requestItem));

        assertTrue(gameSessionIpDAO.findById(1L).isEmpty());

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
            new AuthValidateResponseItem().setState(GameSessionIpState.STARTED),
            objectMapper.readValue(result.getResponse().getContentAsString(), AuthValidateResponse.class)
                .getItems().getFirst()
        );
        assertEquals(1, gameSessionIpDAO.countAll());
        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertEquals(GameSessionIpState.STARTED, gameSessionIpDAO.findById(1L).get().getState());
    }

    @Test
    void validateNotNotifiedTest() throws Exception {
        final AuthValidateRequestItem requestItem = new AuthValidateRequestItem().setName("test-name").setIp("0.0.0.0");
        final AuthValidateRequest request = new AuthValidateRequest().setItems(List.of(requestItem));

        assertTrue(gameSessionIpDAO.findById(1L).isEmpty());

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
            new AuthValidateResponseItem().setState(GameSessionIpState.NOT_NOTIFIED),
            objectMapper.readValue(result.getResponse().getContentAsString(), AuthValidateResponse.class)
                .getItems().getFirst()
        );
        assertEquals(2, gameSessionIpDAO.countAll());
        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertTrue(gameSessionIpDAO.findById(2L).isPresent());
        assertEquals(GameSessionIpState.NOT_NOTIFIED, gameSessionIpDAO.findById(1L).get().getState());
        assertEquals(GameSessionIpState.STARTED, gameSessionIpDAO.findById(2L).get().getState());
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
        final AuthValidateRequestItem requestItem = new AuthValidateRequestItem().setName("test-name").setIp("0.0.0.0");
        final AuthValidateRequest request = new AuthValidateRequest().setItems(List.of(requestItem));

        assertTrue(gameSessionIpDAO.findById(1L).isEmpty());

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
            new AuthValidateResponseItem().setState(GameSessionIpState.STARTED),
            objectMapper.readValue(result.getResponse().getContentAsString(), AuthValidateResponse.class)
                .getItems().getFirst()
        );
        assertEquals(2, gameSessionIpDAO.countAll());
        assertTrue(gameSessionIpDAO.findById(1L).isPresent());
        assertTrue(gameSessionIpDAO.findById(2L).isPresent());
        assertEquals(GameSessionIpState.OUTDATED, gameSessionIpDAO.findById(1L).get().getState());
        assertEquals(GameSessionIpState.STARTED, gameSessionIpDAO.findById(2L).get().getState());
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
    void refreshNoAccountTest() throws Exception {
        final AuthRefreshRequestItem requestItem = new AuthRefreshRequestItem().setIp("0.0.0.0").setName("test-name-x");
        final AuthRefreshRequest request = new AuthRefreshRequest().setItems(List.of(requestItem));

        mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.REFRESH)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1201"));
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

        mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.REFRESH)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("2001"));
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

        mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.REFRESH)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("2001"));
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

        mockMvc.perform(
                MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.REFRESH)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("2001"));
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

        mockMvc.perform(
            MockMvcRequestBuilders.post(AuthController.PREFIX + AuthController.REFRESH)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());
    }
}