package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardOperationRequest {

    @NotEmpty
    protected String actionBy;

    public String getActionBy() {
        return actionBy;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CardOperationRequest{");
        sb.append("actionBy='").append(actionBy).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
