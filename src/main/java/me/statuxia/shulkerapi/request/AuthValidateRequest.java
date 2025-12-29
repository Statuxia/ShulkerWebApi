package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.NonNull;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthValidateRequest {

    @NonNull
    protected List<AuthValidateRequestItem> items;

    @NonNull
    public List<AuthValidateRequestItem> getItems() {
        return items;
    }

    public AuthValidateRequest setItems(@NonNull List<AuthValidateRequestItem> items) {
        this.items = items;
        return this;
    }
}
