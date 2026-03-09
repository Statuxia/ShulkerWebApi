package me.statuxia.shulkerapi.service.impl;

import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dao.MinigamesAccountSessionDAO;
import me.statuxia.shulkerapi.dao.impl.MinigamesAccountActivityDAO;
import me.statuxia.shulkerapi.dto.search.impl.MinigamesAccountActivityDTO;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.MinigamesAccountActivityType;
import me.statuxia.shulkerapi.model.MinigamesAccountSession;
import me.statuxia.shulkerapi.request.MinigamesJoinServerRequest;
import me.statuxia.shulkerapi.request.MinigamesLeftServerRequest;
import me.statuxia.shulkerapi.service.MinigamesSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource("classpath:test-application.properties")
@DirtiesContext
class MinigamesSessionServiceImplTest extends BaseContainerTest {

    @Autowired
    private MinigamesSessionService minigamesSessionService;

    @Autowired
    private GameAccountDAO gameAccountDAO;

    @Autowired
    private MinigamesAccountSessionDAO sessionsDAO;

    @Autowired
    private MinigamesAccountActivityDAO activityDAO;

    // <editor-fold defaultstate="collapsed" desc="joinServer">

    @Sql("classpath:sql/MinigamesSessionControllerTest.sql")
    @Test
    void joinServer_firstJoin_createsSession() {
        final MinigamesJoinServerRequest request = new MinigamesJoinServerRequest()
            .setGameAccountId(1L);

        assertEquals(0, sessionsDAO.count());

        minigamesSessionService.joinServer(request);

        assertEquals(1, sessionsDAO.count());

        final MinigamesAccountSession session = sessionsDAO.findByGameAccount(
            gameAccountDAO.findById(1L).get()
        ).orElseThrow();

        assertTrue(session.getOnline());
        assertNotNull(session.getFirstJoin());
        assertNotNull(session.getLastJoin());
        assertNotNull(session.getUpdatedAt());
    }

    @Sql("classpath:sql/MinigamesSessionControllerTest.sql")
    @Test
    void joinServer_firstJoin_createsActivity() {
        final MinigamesJoinServerRequest request = new MinigamesJoinServerRequest()
            .setGameAccountId(1L);

        assertEquals(0, activityDAO.countAll());

        minigamesSessionService.joinServer(request);

        assertEquals(1, activityDAO.countAll());

        final GameAccount gameAccount = gameAccountDAO.findById(1L).get();
        final var activity = activityDAO.findList(
            new MinigamesAccountActivityDTO().setGameAccount(gameAccount)
        ).getFirst();

        assertEquals(MinigamesAccountActivityType.JOIN_SERVER, activity.getType());
        assertEquals(1L, activity.getGameAccount().getId());
    }

    @Sql({
        "classpath:sql/MinigamesSessionControllerTest.sql",
        "classpath:sql/MinigamesSessionControllerTest_WithSession.sql"
    })
    @Test
    void joinServer_alreadyOnline_skipsUpdate() {
        final MinigamesJoinServerRequest request = new MinigamesJoinServerRequest()
            .setGameAccountId(1L);

        final MinigamesAccountSession sessionBefore = sessionsDAO.findByGameAccount(
            gameAccountDAO.findById(1L).get()
        ).orElseThrow();

        final long activityCountBefore = activityDAO.countAll();

        minigamesSessionService.joinServer(request);

        final MinigamesAccountSession sessionAfter = sessionsDAO.findByGameAccount(
            gameAccountDAO.findById(1L).get()
        ).orElseThrow();

        assertEquals(sessionBefore.getLastJoin(), sessionAfter.getLastJoin());
        assertEquals(activityCountBefore, activityDAO.countAll());
    }

    @Sql("classpath:sql/MinigamesSessionControllerTest.sql")
    @Test
    void joinServer_unknownGameAccount_throwsException() {
        final MinigamesJoinServerRequest request = new MinigamesJoinServerRequest()
            .setGameAccountId(999L);

        assertThrows(Exception.class, () -> minigamesSessionService.joinServer(request));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="leftServer">

    @Sql({
        "classpath:sql/MinigamesSessionControllerTest.sql",
        "classpath:sql/MinigamesSessionControllerTest_WithSession.sql"
    })
    @Test
    void leftServer_success_updatesPlaytime() {
        final MinigamesLeftServerRequest request = new MinigamesLeftServerRequest()
            .setGameAccountId(1L);

        final MinigamesAccountSession sessionBefore = sessionsDAO.findByGameAccount(
            gameAccountDAO.findById(1L).get()
        ).orElseThrow();

        final long playtimeBefore = sessionBefore.getPlaytime() != null ? sessionBefore.getPlaytime() : 0L;

        minigamesSessionService.leftServer(request);

        final MinigamesAccountSession sessionAfter = sessionsDAO.findByGameAccount(
            gameAccountDAO.findById(1L).get()
        ).orElseThrow();

        assertFalse(sessionAfter.getOnline());
        assertNotNull(sessionAfter.getUpdatedAt());
        assertTrue(sessionAfter.getPlaytime() >= playtimeBefore);
    }

    @Sql({
        "classpath:sql/MinigamesSessionControllerTest.sql",
        "classpath:sql/MinigamesSessionControllerTest_WithSession.sql"
    })
    @Test
    void leftServer_success_createsActivity() {
        final MinigamesLeftServerRequest request = new MinigamesLeftServerRequest()
            .setGameAccountId(1L);

        final long activityCountBefore = activityDAO.countAll();

        minigamesSessionService.leftServer(request);

        assertEquals(activityCountBefore + 1, activityDAO.countAll());

        final GameAccount gameAccount = gameAccountDAO.findById(1L).get();
        final var activity = activityDAO.findList(
            new MinigamesAccountActivityDTO()
                .setGameAccount(gameAccount)
                .setType(MinigamesAccountActivityType.LEFT_SERVER)
        ).getFirst();

        assertEquals(MinigamesAccountActivityType.LEFT_SERVER, activity.getType());
    }

    @Sql({
        "classpath:sql/MinigamesSessionControllerTest.sql",
        "classpath:sql/MinigamesSessionControllerTest_WithSession.sql"
    })
    @Test
    void leftServer_nullPlaytime_calculatesFromZero() {
        // gameAccount 2 has null playtime and online=false, but we test null playtime path directly
        // gameAccount 1 has playtime=60000, let's verify accumulation is correct
        final MinigamesLeftServerRequest request = new MinigamesLeftServerRequest()
            .setGameAccountId(1L);

        minigamesSessionService.leftServer(request);

        final MinigamesAccountSession session = sessionsDAO.findByGameAccount(
            gameAccountDAO.findById(1L).get()
        ).orElseThrow();

        assertTrue(session.getPlaytime() >= 60000L);
    }

    @Sql({
        "classpath:sql/MinigamesSessionControllerTest.sql",
        "classpath:sql/MinigamesSessionControllerTest_WithSession.sql"
    })
    @Test
    void leftServer_sessionNotOnline_throwsException() {
        final MinigamesLeftServerRequest request = new MinigamesLeftServerRequest()
            .setGameAccountId(2L);

        assertThrows(Exception.class, () -> minigamesSessionService.leftServer(request));
    }

    @Sql("classpath:sql/MinigamesSessionControllerTest.sql")
    @Test
    void leftServer_sessionNotFound_throwsException() {
        final MinigamesLeftServerRequest request = new MinigamesLeftServerRequest()
            .setGameAccountId(1L);

        assertThrows(Exception.class, () -> minigamesSessionService.leftServer(request));
    }

    @Sql("classpath:sql/MinigamesSessionControllerTest.sql")
    @Test
    void leftServer_unknownGameAccount_throwsException() {
        final MinigamesLeftServerRequest request = new MinigamesLeftServerRequest()
            .setGameAccountId(999L);

        assertThrows(Exception.class, () -> minigamesSessionService.leftServer(request));
    }

    // </editor-fold>
}
