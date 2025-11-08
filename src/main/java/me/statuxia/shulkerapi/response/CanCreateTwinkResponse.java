package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CanCreateTwinkResponse {

    protected boolean canCreate;
    protected Long discordId;

    public boolean isCanCreate() {
        return canCreate;
    }

    public CanCreateTwinkResponse setCanCreate(boolean canCreate) {
        this.canCreate = canCreate;
        return this;
    }

    public Long getDiscordId() {
        return discordId;
    }

    public CanCreateTwinkResponse setDiscordId(Long discordId) {
        this.discordId = discordId;
        return this;
    }
}
