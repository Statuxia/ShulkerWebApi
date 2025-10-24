package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import me.statuxia.shulkerapi.model.Fine;
import me.statuxia.shulkerapi.model.FineStatus;
import org.joda.time.DateTime;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FineItem {

    private Long id;
    private Long fineValue;
    private String message;
    private DateTime createDate;
    private FineStatus status;
    private DateTime statusDate;
    private DateTime dueDate;
    private String actionBy;
    private Boolean notified;
    private List<FineLogItem> logs;

    public FineItem(Fine fine) {
        id = fine.getId();
        fineValue = fine.getFineValue();
        message = fine.getMessage();
        createDate = fine.getCreateDate();
        status = fine.getStatus();
        statusDate = fine.getStatusDate();
        dueDate = fine.getDueDate();
        actionBy = fine.getActionBy();
        notified = fine.isNotified();
    }

    public Long getId() {
        return id;
    }

    public FineItem setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getFineValue() {
        return fineValue;
    }

    public FineItem setFineValue(Long fineValue) {
        this.fineValue = fineValue;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public FineItem setMessage(String message) {
        this.message = message;
        return this;
    }

    public DateTime getCreateDate() {
        return createDate;
    }

    public FineItem setCreateDate(DateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public FineStatus getStatus() {
        return status;
    }

    public FineItem setStatus(FineStatus status) {
        this.status = status;
        return this;
    }

    public DateTime getStatusDate() {
        return statusDate;
    }

    public FineItem setStatusDate(DateTime statusDate) {
        this.statusDate = statusDate;
        return this;
    }

    public DateTime getDueDate() {
        return dueDate;
    }

    public FineItem setDueDate(DateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public String getActionBy() {
        return actionBy;
    }

    public FineItem setActionBy(String actionBy) {
        this.actionBy = actionBy;
        return this;
    }

    public Boolean getNotified() {
        return notified;
    }

    public FineItem setNotified(Boolean notified) {
        this.notified = notified;
        return this;
    }

    public List<FineLogItem> getLogs() {
        return logs;
    }

    public FineItem setLogs(List<FineLogItem> logs) {
        this.logs = logs;
        return this;
    }
}
