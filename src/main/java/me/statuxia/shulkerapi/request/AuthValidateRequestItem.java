package me.statuxia.shulkerapi.request;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.NonNull;

public class AuthValidateRequestItem {

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

    public AuthValidateRequestItem setName(@NonNull String name) {
        this.name = name;
        return this;
    }

    @NonNull
    public String getIp() {
        return ip;
    }

    public AuthValidateRequestItem setIp(@NonNull String ip) {
        this.ip = ip;
        return this;
    }
}
