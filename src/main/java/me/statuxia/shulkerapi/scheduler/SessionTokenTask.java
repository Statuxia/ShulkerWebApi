package me.statuxia.shulkerapi.scheduler;

import me.statuxia.shulkerapi.configuration.properties.SessionTokenProperties;
import me.statuxia.shulkerapi.dao.SessionTokenDAO;
import me.statuxia.shulkerapi.model.SessionToken;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class SessionTokenTask {

    public static final int BATCH_SIZE = 200;
    private final SessionTokenDAO sessionTokenDAO;
    private final SessionTokenProperties sessionTokenProperties;

    @Autowired
    public SessionTokenTask(SessionTokenDAO sessionTokenDAO, SessionTokenProperties sessionTokenProperties) {
        this.sessionTokenDAO = sessionTokenDAO;
        this.sessionTokenProperties = sessionTokenProperties;
    }

    @Scheduled(initialDelay = 15L, fixedRate = 5 * 60, timeUnit = TimeUnit.SECONDS)
    public void process() {
        final DateTime createTime = DateTime.now().minusDays(sessionTokenProperties.getDaysForDisable());
        final List<SessionToken> outdatedTokens = sessionTokenDAO.findByCreateTimeLessThanEqualAndDisabledFalse(
            createTime, Pageable.ofSize(BATCH_SIZE)
        );

        outdatedTokens.forEach(outdatedToken -> {
            outdatedToken.setDisabled(true);
            outdatedToken.setDisabledTime(DateTime.now());
        });

        outdatedTokens.forEach(sessionTokenDAO::save);
    }
}
