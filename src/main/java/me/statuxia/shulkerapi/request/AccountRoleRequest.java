package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountRoleRequest {

    @NotNull
    @Size(min = 1, max = 50)
    private List<Long> discordIds;

    public List<Long> getDiscordIds() {
        return discordIds;
    }

    public void setDiscordIds(List<Long> discordIds) {
        this.discordIds = discordIds;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DiscordAccountRequest{");
        sb.append("discordIds=").append(discordIds);
        sb.append('}');
        return sb.toString();
    }
}
