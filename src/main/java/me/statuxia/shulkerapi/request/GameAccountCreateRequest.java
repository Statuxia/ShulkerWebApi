package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameAccountCreateRequest {

    private String name;
    private Long discordId;
    private boolean twink;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(Long discordId) {
        this.discordId = discordId;
    }

    public boolean isTwink() {
        return twink;
    }

    public void setTwink(boolean twink) {
        this.twink = twink;
    }
}
