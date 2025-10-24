package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EditFineRequest {

    @NotNull
    protected Long id;
    @NotNull
    @NotEmpty
    protected String actionBy;

    public Long getId() {
        return id;
    }

    public EditFineRequest setId(Long id) {
        this.id = id;
        return this;
    }

    public String getActionBy() {
        return actionBy;
    }

    public EditFineRequest setActionBy(String actionBy) {
        this.actionBy = actionBy;
        return this;
    }
}
