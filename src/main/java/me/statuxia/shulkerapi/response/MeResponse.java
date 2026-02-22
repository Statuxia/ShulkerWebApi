package me.statuxia.shulkerapi.response;

import java.util.List;

public class MeResponse {

    private DiscordIdentityResponse discord;
    private List<MeGameAccountResponse> gameAccounts;
    private List<Long> roles;

    public DiscordIdentityResponse getDiscord() {
        return discord;
    }

    public MeResponse setDiscord(DiscordIdentityResponse discord) {
        this.discord = discord;
        return this;
    }

    public List<MeGameAccountResponse> getGameAccounts() {
        return gameAccounts;
    }

    public MeResponse setGameAccounts(List<MeGameAccountResponse> gameAccounts) {
        this.gameAccounts = gameAccounts;
        return this;
    }

    public List<Long> getRoles() {
        return roles;
    }

    public MeResponse setRoles(List<Long> roles) {
        this.roles = roles;
        return this;
    }
}
