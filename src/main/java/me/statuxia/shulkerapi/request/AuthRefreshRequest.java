package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthRefreshRequest {

    @NonNull
    @NotEmpty
    protected String name;
    @NonNull
    @NotEmpty
    protected String ip;

    @NonNull
    public String getName() {
        return name;
    }

    public AuthRefreshRequest setName(@NonNull String name) {
        this.name = name;
        return this;
    }

    @NonNull
    public String getIp() {
        return ip;
    }

    public AuthRefreshRequest setIp(@NonNull String ip) {
        this.ip = ip;
        return this;
    }
}
