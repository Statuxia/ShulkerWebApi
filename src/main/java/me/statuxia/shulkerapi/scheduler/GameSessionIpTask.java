package me.statuxia.shulkerapi.scheduler;

import me.statuxia.shulkerapi.dao.impl.GameSessionIpDAO;
import me.statuxia.shulkerapi.dto.search.impl.GameSessionIpDTO;
import me.statuxia.shulkerapi.model.GameSessionIp;
import me.statuxia.shulkerapi.model.GameSessionIpState;
import org.joda.time.DateTime;
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

    public static final int BATCH_SIZE = 50;
    private final GameSessionIpDAO gameSessionIpDAO;
    private final List<GameSessionIpState> states = Arrays.stream(
            GameSessionIpState.values())
        .filter(k -> k.equals(GameSessionIpState.OUTDATED)).toList();

    @Autowired
    public GameSessionIpTask(GameSessionIpDAO gameSessionIpDAO) {
        this.gameSessionIpDAO = gameSessionIpDAO;
    }

    @Scheduled(initialDelayString = "36s", fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void process() {
        final GameSessionIpDTO dto = new GameSessionIpDTO()
            .setStates(states)
            .setLastJoinDateTo(DateTime.now())
            .setPageable(Pageable.ofSize(BATCH_SIZE));
        final List<GameSessionIp> list = gameSessionIpDAO.findList(dto);

        for (GameSessionIp gameSessionIp : list) {
            gameSessionIp.setNotified(true);
            gameSessionIp.setState(GameSessionIpState.OUTDATED);
        }

        gameSessionIpDAO.saveAll(list);
    }
}
