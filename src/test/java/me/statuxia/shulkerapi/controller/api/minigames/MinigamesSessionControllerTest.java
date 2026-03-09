package me.statuxia.shulkerapi.controller.api.minigames;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dao.MinigamesAccountSessionDAO;
import me.statuxia.shulkerapi.dao.impl.MinigamesAccountActivityDAO;
import me.statuxia.shulkerapi.dto.search.impl.MinigamesAccountActivityDTO;
import me.statuxia.shulkerapi.model.MinigamesAccountActivityType;
import me.statuxia.shulkerapi.model.MinigamesAccountSession;
import me.statuxia.shulkerapi.request.MinigamesJoinServerRequest;
import me.statuxia.shulkerapi.request.MinigamesLeftServerRequest;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:sql/MinigamesSessionControllerTest.sql")
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
class MinigamesSessionControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "custom-token-1";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected GameAccountDAO gameAccountDAO;

    @Autowired
    protected MinigamesAccountSessionDAO sessionsDAO;

    @Autowired
    protected MinigamesAccountActivityDAO activityDAO;

    // <editor-fold defaultstate="collapsed" desc="joinTest">

    @Test
    void joinTest_success_firstJoin() throws Exception {
        final MinigamesJoinServerRequest request = new MinigamesJoinServerRequest()
            .setGameAccountId(1L);

        assertEquals(0, sessionsDAO.count());
        assertEquals(0, activityDAO.countAll());

        mockMvc.perform(
            MockMvcRequestBuilders.post(MinigamesSessionController.PREFIX + MinigamesSessionController.JOIN)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        assertEquals(1, sessionsDAO.count());

        final MinigamesAccountSession session = sessionsDAO.findByGameAccount(
            gameAccountDAO.findById(1L).get()
        ).orElseThrow();

        assertTrue(session.getOnline());
        assertNotNull(session.getFirstJoin());
        assertNotNull(session.getLastJoin());
        assertNotNull(session.getUpdatedAt());

        assertEquals(1, activityDAO.countAll());
        assertEquals(
            MinigamesAccountActivityType.JOIN_SERVER,
            activityDAO.findList(new MinigamesAccountActivityDTO()
                .setGameAccount(gameAccountDAO.findById(1L).get())
            ).getFirst().getType()
        );
    }

    @Test
    void joinTest_unknownGameAccount() throws Exception {
        final MinigamesJoinServerRequest request = new MinigamesJoinServerRequest()
            .setGameAccountId(999L);

        mockMvc.perform(
                MockMvcRequestBuilders.post(MinigamesSessionController.PREFIX + MinigamesSessionController.JOIN)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1201"));

        assertEquals(0, sessionsDAO.count());
        assertEquals(0, activityDAO.countAll());
    }

    @Test
    void joinTest_noAuthority() throws Exception {
        final MinigamesJoinServerRequest request = new MinigamesJoinServerRequest()
            .setGameAccountId(1L);

        mockMvc.perform(
                MockMvcRequestBuilders.post(MinigamesSessionController.PREFIX + MinigamesSessionController.JOIN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized());

        assertEquals(0, sessionsDAO.count());
    }

    @Sql({
        "classpath:sql/MinigamesSessionControllerTest.sql",
        "classpath:sql/MinigamesSessionControllerTest_WithSession.sql"
    })
    @Test
    void joinTest_alreadyOnline_skips() throws Exception {
        final MinigamesJoinServerRequest request = new MinigamesJoinServerRequest()
            .setGameAccountId(1L);

        final MinigamesAccountSession sessionBefore = sessionsDAO.findByGameAccount(
            gameAccountDAO.findById(1L).get()
        ).orElseThrow();

        final long activityCountBefore = activityDAO.countAll();

        mockMvc.perform(
            MockMvcRequestBuilders.post(MinigamesSessionController.PREFIX + MinigamesSessionController.JOIN)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        final MinigamesAccountSession sessionAfter = sessionsDAO.findByGameAccount(
            gameAccountDAO.findById(1L).get()
        ).orElseThrow();

        assertEquals(sessionBefore.getLastJoin(), sessionAfter.getLastJoin());
        assertEquals(activityCountBefore, activityDAO.countAll());
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="leftTest">

    @Sql({
        "classpath:sql/MinigamesSessionControllerTest.sql",
        "classpath:sql/MinigamesSessionControllerTest_WithSession.sql"
    })
    @Test
    void leftTest_success() throws Exception {
        final MinigamesLeftServerRequest request = new MinigamesLeftServerRequest()
            .setGameAccountId(1L);

        final MinigamesAccountSession sessionBefore = sessionsDAO.findByGameAccount(
            gameAccountDAO.findById(1L).get()
        ).orElseThrow();

        final long activityCountBefore = activityDAO.countAll();

        mockMvc.perform(
            MockMvcRequestBuilders.post(MinigamesSessionController.PREFIX + MinigamesSessionController.LEFT)
                .header(X_TOKEN_HEADER, SESSION_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        final MinigamesAccountSession sessionAfter = sessionsDAO.findByGameAccount(
            gameAccountDAO.findById(1L).get()
        ).orElseThrow();

        assertFalse(sessionAfter.getOnline());
        assertNotNull(sessionAfter.getUpdatedAt());
        assertTrue(sessionAfter.getPlaytime() >= (sessionBefore.getPlaytime() != null ? sessionBefore.getPlaytime() : 0L));

        assertEquals(activityCountBefore + 1, activityDAO.countAll());
        assertEquals(
            MinigamesAccountActivityType.LEFT_SERVER,
            activityDAO.findList(new MinigamesAccountActivityDTO()
                .setGameAccount(gameAccountDAO.findById(1L).get())
                .setType(MinigamesAccountActivityType.LEFT_SERVER)
            ).getFirst().getType()
        );
    }

    @Test
    void leftTest_sessionNotFound() throws Exception {
        final MinigamesLeftServerRequest request = new MinigamesLeftServerRequest()
            .setGameAccountId(1L);

        mockMvc.perform(
                MockMvcRequestBuilders.post(MinigamesSessionController.PREFIX + MinigamesSessionController.LEFT)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("3000"));
    }

    @Sql({
        "classpath:sql/MinigamesSessionControllerTest.sql",
        "classpath:sql/MinigamesSessionControllerTest_WithSession.sql"
    })
    @Test
    void leftTest_sessionNotOnline() throws Exception {
        final MinigamesLeftServerRequest request = new MinigamesLeftServerRequest()
            .setGameAccountId(2L);

        mockMvc.perform(
                MockMvcRequestBuilders.post(MinigamesSessionController.PREFIX + MinigamesSessionController.LEFT)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("3000"));
    }

    @Test
    void leftTest_unknownGameAccount() throws Exception {
        final MinigamesLeftServerRequest request = new MinigamesLeftServerRequest()
            .setGameAccountId(999L);

        mockMvc.perform(
                MockMvcRequestBuilders.post(MinigamesSessionController.PREFIX + MinigamesSessionController.LEFT)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1201"));
    }

    @Test
    void leftTest_noAuthority() throws Exception {
        final MinigamesLeftServerRequest request = new MinigamesLeftServerRequest()
            .setGameAccountId(1L);

        mockMvc.perform(
                MockMvcRequestBuilders.post(MinigamesSessionController.PREFIX + MinigamesSessionController.LEFT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized());
    }

    // </editor-fold>
}
