package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class QueueScheduleSearchDTO implements ISearchDTO {

    protected Long id;
    protected String type;
    protected Boolean completed;
    protected List<Long> ids;
    protected DateTime createdAfter;
    protected Pageable pageable;

    public Long getId() {
        return id;
    }

    public QueueScheduleSearchDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public QueueScheduleSearchDTO setType(String type) {
        this.type = type;
        return this;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public QueueScheduleSearchDTO setCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public List<Long> getIds() {
        return ids;
    }

    public QueueScheduleSearchDTO setIds(List<Long> ids) {
        this.ids = ids;
        return this;
    }

    public DateTime getCreatedAfter() {
        return createdAfter;
    }

    public QueueScheduleSearchDTO setCreatedAfter(DateTime createdAfter) {
        this.createdAfter = createdAfter;
        return this;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public QueueScheduleSearchDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QueueScheduleSearchDTO{");
        sb.append("type='").append(type).append('\'');
        sb.append(", ids=").append(ids);
        sb.append(", createdAfter=").append(createdAfter);
        sb.append(", pageable=").append(pageable);
        sb.append('}');
        return sb.toString();
    }
}