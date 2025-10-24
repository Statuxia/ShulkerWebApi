package me.statuxia.shulkerapi.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class EditMessageFineRequest extends EditFineRequest {

    @NotNull
    @NotEmpty
    @Length(max = 500)
    protected String message;

    public String getMessage() {
        return message;
    }

    public EditMessageFineRequest setMessage(String message) {
        this.message = message;
        return this;
    }
}
