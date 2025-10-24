package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.FineStatus;
import me.statuxia.shulkerapi.model.GameAccount;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class FineSearchDTO implements ISearchDTO {

    protected GameAccount gameAccount;
    protected List<FineStatus> statuses;
    protected String actionBy;
    protected boolean unnotifiedOnly;

    protected Pageable pageable;

    protected DateTime dueDateFrom;
    protected DateTime dueDateTo;

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public FineSearchDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

    public GameAccount getGameAccount() {
        return gameAccount;
    }

    public FineSearchDTO setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }

    public List<FineStatus> getStatuses() {
        return statuses;
    }

    public FineSearchDTO setStatuses(List<FineStatus> statuses) {
        this.statuses = statuses;
        return this;
    }

    public String getActionBy() {
        return actionBy;
    }

    public FineSearchDTO setActionBy(String actionBy) {
        this.actionBy = actionBy;
        return this;
    }

    public boolean isUnnotifiedOnly() {
        return unnotifiedOnly;
    }

    public FineSearchDTO setUnnotifiedOnly(boolean unnotifiedOnly) {
        this.unnotifiedOnly = unnotifiedOnly;
        return this;
    }

    public DateTime getDueDateFrom() {
        return dueDateFrom;
    }

    public FineSearchDTO setDueDateFrom(DateTime dueDateFrom) {
        this.dueDateFrom = dueDateFrom;
        return this;
    }

    public DateTime getDueDateTo() {
        return dueDateTo;
    }

    public FineSearchDTO setDueDateTo(DateTime dueDateTo) {
        this.dueDateTo = dueDateTo;
        return this;
    }
}
