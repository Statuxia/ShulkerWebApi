package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dao.MinigamesAccountSessionDAO;
import me.statuxia.shulkerapi.dao.impl.MinigamesAccountActivityDAO;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.exception.MinigamesSessionException;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.MinigamesAccountActivity;
import me.statuxia.shulkerapi.model.MinigamesAccountActivityType;
import me.statuxia.shulkerapi.model.MinigamesAccountSession;
import me.statuxia.shulkerapi.request.MinigamesJoinServerRequest;
import me.statuxia.shulkerapi.request.MinigamesLeftServerRequest;
import me.statuxia.shulkerapi.service.MinigamesSessionService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MinigamesSessionServiceImpl implements MinigamesSessionService {
    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GameAccountDAO gameAccountDAO;
    private final MinigamesAccountSessionDAO sessionsDAO;
    private final MinigamesAccountActivityDAO activityDAO;

    @Autowired
    public MinigamesSessionServiceImpl(
        GameAccountDAO gameAccountDAO,
        MinigamesAccountSessionDAO sessionsDAO,
        MinigamesAccountActivityDAO activityDAO
    ) {
        this.gameAccountDAO = gameAccountDAO;
        this.sessionsDAO = sessionsDAO;
        this.activityDAO = activityDAO;
    }

    @Override
    public void joinServer(MinigamesJoinServerRequest request) {
        final GameAccount gameAccount = gameAccountDAO.findById(request.getGameAccountId())
            .orElseThrow(() -> AccountException.UNKNOWN_ACCOUNT);
        final DateTime now = DateTime.now();
        final MinigamesAccountSession session = sessionsDAO.findByGameAccount(gameAccount).orElseGet(() -> {
            final MinigamesAccountSession newSession = new MinigamesAccountSession();
            newSession.setGameAccount(gameAccount);
            newSession.setFirstJoin(now);
            return newSession;
        });

        if (Boolean.TRUE.equals(session.getOnline())) {
            logger.debug("[account #{}] session already started. Skip", gameAccount.getId());
            return;
        }

        session.setOnline(true);
        session.setLastJoin(now);
        session.setUpdatedAt(now);

        sessionsDAO.save(session);

        final MinigamesAccountActivity activity = new MinigamesAccountActivity();
        activity.setGameAccount(gameAccount);
        activity.setActivityAt(now);
        activity.setType(MinigamesAccountActivityType.JOIN_SERVER);

        activityDAO.save(activity);
    }

    @Override
    public void leftServer(MinigamesLeftServerRequest request) {
        final GameAccount gameAccount = gameAccountDAO.findById(request.getGameAccountId())
            .orElseThrow(() -> AccountException.UNKNOWN_ACCOUNT);
        final DateTime now = DateTime.now();
        final MinigamesAccountSession session = sessionsDAO.findByGameAccount(gameAccount)
            .orElseThrow(() -> MinigamesSessionException.SESSION_NOT_STARTED);

        if (!Boolean.TRUE.equals(session.getOnline())) {
            throw MinigamesSessionException.SESSION_NOT_STARTED;
        }

        final long sessionDuration = session.getLastJoin() == null
            ? 0 : now.getMillis() - session.getLastJoin().getMillis();
        final long currentPlaytime = session.getPlaytime() != null ? session.getPlaytime() : 0L;

        session.setPlaytime(currentPlaytime + sessionDuration);
        session.setOnline(false);
        session.setUpdatedAt(now);

        sessionsDAO.save(session);

        final MinigamesAccountActivity activity = new MinigamesAccountActivity();
        activity.setGameAccount(gameAccount);
        activity.setActivityAt(now);
        activity.setType(MinigamesAccountActivityType.LEFT_SERVER);

        activityDAO.save(activity);
    }
}
