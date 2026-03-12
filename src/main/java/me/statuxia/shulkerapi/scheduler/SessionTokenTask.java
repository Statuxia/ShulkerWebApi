package me.statuxia.shulkerapi.scheduler;

import me.statuxia.shulkerapi.configuration.properties.SessionTokenProperties;
import me.statuxia.shulkerapi.dao.SessionTokenDAO;
import me.statuxia.shulkerapi.model.Identifiable;
import me.statuxia.shulkerapi.model.SessionToken;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class SessionTokenTask {
    public final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        final List<SessionToken> badTokens = new ArrayList<>();

        outdatedTokens.forEach(token -> {
            if (token.getAccount() == null || token.getAccount().getDiscordAccount() == null) {
                badTokens.add(token);
            }
        });

        outdatedTokens.removeAll(badTokens);
        deleteBadTokens(badTokens);

        outdatedTokens.forEach(outdatedToken -> {
            outdatedToken.setDisabled(true);
            outdatedToken.setDisabledTime(DateTime.now());
        });

        outdatedTokens.forEach(sessionTokenDAO::save);
    }

    private void deleteBadTokens(List<SessionToken> tokens) {
        final List<Long> ids = tokens.stream()
            .filter(Objects::nonNull).map(Identifiable::getId)
            .toList();
        sessionTokenDAO.deleteAllById(ids);
        logger.debug("deleted {} sessions with unknown game account", ids.size());
    }

}
