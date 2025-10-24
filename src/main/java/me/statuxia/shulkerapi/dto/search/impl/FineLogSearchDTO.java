package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.Fine;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class FineLogSearchDTO implements ISearchDTO {

    protected Fine fine;
    protected List<Fine> fineList;
    protected String actionBy;

    protected Pageable pageable;

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public FineLogSearchDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

    public Fine getFine() {
        return fine;
    }

    public FineLogSearchDTO setFine(Fine fine) {
        this.fine = fine;
        return this;
    }

    public String getActionBy() {
        return actionBy;
    }

    public FineLogSearchDTO setActionBy(String actionBy) {
        this.actionBy = actionBy;
        return this;
    }

    public List<Fine> getFineList() {
        return fineList;
    }

    public FineLogSearchDTO setFineList(List<Fine> fineList) {
        this.fineList = fineList;
        return this;
    }
}
