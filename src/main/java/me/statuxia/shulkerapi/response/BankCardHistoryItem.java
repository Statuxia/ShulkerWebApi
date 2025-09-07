package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import me.statuxia.shulkerapi.model.BankOperationState;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankCardHistoryItem {

    protected Long id;
    protected String type;
    protected String dateTime;
    @Schema(description = "i18n данные, можно возвращать как есть")
    protected Map<String, String> data;
    protected List<BankCardHistoryLogItem> logs;
    protected BankOperationState state;

    public Long getId() {
        return id;
    }

    public BankCardHistoryItem setId(Long id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public BankCardHistoryItem setType(String type) {
        this.type = type;
        return this;
    }

    public String getDateTime() {
        return dateTime;
    }

    public BankCardHistoryItem setDateTime(String dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public Map<String, String> getData() {
        return data;
    }

    public BankCardHistoryItem setData(Map<String, String> data) {
        this.data = data;
        return this;
    }

    public List<BankCardHistoryLogItem> getLogs() {
        return logs;
    }

    public void setLogs(List<BankCardHistoryLogItem> logs) {
        this.logs = logs;
    }

    public BankOperationState getState() {
        return state;
    }

    public BankCardHistoryItem setState(BankOperationState state) {
        this.state = state;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BankCardHistoryItem item = (BankCardHistoryItem) o;
        return Objects.equals(getId(), item.getId())
            && Objects.equals(getType(), item.getType())
            && Objects.equals(getDateTime(), item.getDateTime())
            && Objects.equals(getData(), item.getData())
            && Objects.equals(getLogs(), item.getLogs())
            && getState() == item.getState();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType(), getDateTime(), getData(), getLogs(), getState());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BankCardHistoryItem{");
        sb.append("id=").append(id);
        sb.append(", type='").append(type).append('\'');
        sb.append(", dateTime='").append(dateTime).append('\'');
        sb.append(", data=").append(data);
        sb.append(", logs=").append(logs);
        sb.append(", state=").append(state);
        sb.append('}');
        return sb.toString();
    }
}
