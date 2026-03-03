package me.statuxia.shulkerapi.scheduler;

import me.statuxia.shulkerapi.dao.impl.GameSessionIpDAO;
import me.statuxia.shulkerapi.dto.search.impl.GameSessionIpDTO;
import me.statuxia.shulkerapi.model.GameSessionIp;
import me.statuxia.shulkerapi.model.GameSessionIpState;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Transactional
public class GameSessionIpTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final int BATCH_SIZE = 50;
    private final GameSessionIpDAO gameSessionIpDAO;
    private final List<GameSessionIpState> states = Arrays.stream(
            GameSessionIpState.values())
        .filter(k -> !k.equals(GameSessionIpState.OUTDATED)).toList();

    @Autowired
    public GameSessionIpTask(GameSessionIpDAO gameSessionIpDAO) {
        this.gameSessionIpDAO = gameSessionIpDAO;
    }

    @Scheduled(initialDelay = 36L, fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void process() {
        final GameSessionIpDTO dto = new GameSessionIpDTO()
            .setStates(states)
            .setLastJoinDateTo(DateTime.now())
            .setPageable(Pageable.ofSize(BATCH_SIZE));

        logger.debug("dto: {}", dto);
        final List<GameSessionIp> list = gameSessionIpDAO.findList(dto);

        logger.debug("list size: {}", list.size());
        for (GameSessionIp gameSessionIp : list) {
            gameSessionIp.setNotified(true);
            gameSessionIp.setState(GameSessionIpState.OUTDATED);
        }

        gameSessionIpDAO.saveAll(list);
    }
}
