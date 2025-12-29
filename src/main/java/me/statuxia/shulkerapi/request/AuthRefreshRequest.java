package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.NonNull;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthRefreshRequest {

    @NonNull
    protected List<AuthRefreshRequestItem> items;

    @NonNull
    public List<AuthRefreshRequestItem> getItems() {
        return items;
    }

    public AuthRefreshRequest setItems(@NonNull List<AuthRefreshRequestItem> items) {
        this.items = items;
        return this;
    }
}
