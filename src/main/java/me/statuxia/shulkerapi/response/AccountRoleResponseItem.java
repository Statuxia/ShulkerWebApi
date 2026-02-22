package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountRoleResponseItem {

    @NotNull
    private Long id;
    private List<Long> roles;

    public Long getId() {
        return id;
    }

    public AccountRoleResponseItem setId(Long id) {
        this.id = id;
        return this;
    }

    public List<Long> getRoles() {
        return roles;
    }

    public AccountRoleResponseItem setRoles(List<Long> roles) {
        this.roles = roles;
        return this;
    }
}
