package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateFineRequest {

    @NotNull
    @NotEmpty
    protected String gameAccount;
    @NotNull
    @Min(1)
    protected Long value;
    @Length(max = 500)
    @NotNull
    protected String message;

    @Schema(example = "01.01.2025 00:00:00")
    @NotNull
    protected DateTime dueDate;

    @NotNull
    @NotEmpty
    protected String actionBy;

    public String getGameAccount() {
        return gameAccount;
    }

    public CreateFineRequest setGameAccount(String gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }

    public Long getValue() {
        return value;
    }

    public CreateFineRequest setValue(Long value) {
        this.value = value;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CreateFineRequest setMessage(String message) {
        this.message = message;
        return this;
    }

    public DateTime getDueDate() {
        return dueDate;
    }

    public CreateFineRequest setDueDate(DateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public String getActionBy() {
        return actionBy;
    }

    public CreateFineRequest setActionBy(String actionBy) {
        this.actionBy = actionBy;
        return this;
    }
}
