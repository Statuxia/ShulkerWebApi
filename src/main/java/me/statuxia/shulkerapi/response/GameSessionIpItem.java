package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import me.statuxia.shulkerapi.model.GameSessionIpState;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameSessionIpItem {
    protected Long id;
    protected String name;
    protected Long discordId;
    protected String ip;
    protected GameSessionIpState state;

    public Long getId() {
        return id;
    }

    public GameSessionIpItem setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GameSessionIpItem setName(String name) {
        this.name = name;
        return this;
    }

    public Long getDiscordId() {
        return discordId;
    }

    public GameSessionIpItem setDiscordId(Long discordId) {
        this.discordId = discordId;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public GameSessionIpItem setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public GameSessionIpState getState() {
        return state;
    }

    public GameSessionIpItem setState(GameSessionIpState state) {
        this.state = state;
        return this;
    }
}
