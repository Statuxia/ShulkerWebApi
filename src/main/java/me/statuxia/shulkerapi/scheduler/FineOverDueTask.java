package me.statuxia.shulkerapi.scheduler;

import me.statuxia.shulkerapi.dao.impl.FineDAO;
import me.statuxia.shulkerapi.dto.search.impl.FineSearchDTO;
import me.statuxia.shulkerapi.model.Fine;
import me.statuxia.shulkerapi.model.FineStatus;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Transactional
public class FineOverDueTask {

    public static final int BATCH_SIZE = 200;

    private final FineDAO fineDAO;

    @Autowired
    public FineOverDueTask(FineDAO fineDAO) {
        this.fineDAO = fineDAO;
    }


    @Scheduled(initialDelay = 35L, fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void process() {
        final List<Fine> fines = fineDAO.findList(new FineSearchDTO()
            .setStatuses(List.of(FineStatus.NEW))
            .setPageable(Pageable.ofSize(BATCH_SIZE))
            .setDueDateTo(DateTime.now()));

        fines.forEach(fine -> {
            fine.setStatus(FineStatus.OVERDUE);
            fine.setStatusDate(DateTime.now());
        });

        fineDAO.saveAll(fines);
    }
}
