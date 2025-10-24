package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.joda.time.DateTime;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FineLogItem {

    private String action;
    private String actionBy;
    private DateTime logDate;
    private Map<String, String> data;

    public String getActionBy() {
        return actionBy;
    }

    public FineLogItem setActionBy(String actionBy) {
        this.actionBy = actionBy;
        return this;
    }

    public String getAction() {
        return action;
    }

    public FineLogItem setAction(String action) {
        this.action = action;
        return this;
    }

    public DateTime getLogDate() {
        return logDate;
    }

    public FineLogItem setLogDate(DateTime logDate) {
        this.logDate = logDate;
        return this;
    }

    public Map<String, String> getData() {
        return data;
    }

    public FineLogItem setData(Map<String, String> data) {
        this.data = data;
        return this;
    }
}
