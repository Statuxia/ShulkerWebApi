package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.GameSessionIpState;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class GameSessionIpDTO implements ISearchDTO {

    protected GameAccount gameAccount;
    protected String ip;
    protected boolean notified;
    protected List<GameSessionIpState> states;
    protected DateTime lastJoinDateFrom;
    protected DateTime lastJoinDateTo;

    protected Pageable pageable;

    public GameAccount getGameAccount() {
        return gameAccount;
    }

    public GameSessionIpDTO setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public GameSessionIpDTO setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Boolean isNotified() {
        return notified;
    }

    public GameSessionIpDTO setNotified(Boolean notified) {
        this.notified = notified;
        return this;
    }

    public DateTime getLastJoinDateFrom() {
        return lastJoinDateFrom;
    }

    public GameSessionIpDTO setLastJoinDateFrom(DateTime lastJoinDateFrom) {
        this.lastJoinDateFrom = lastJoinDateFrom;
        return this;
    }

    public DateTime getLastJoinDateTo() {
        return lastJoinDateTo;
    }

    public GameSessionIpDTO setLastJoinDateTo(DateTime lastJoinDateTo) {
        this.lastJoinDateTo = lastJoinDateTo;
        return this;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public GameSessionIpDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

    public List<GameSessionIpState> getStates() {
        return states;
    }

    public GameSessionIpDTO setStates(List<GameSessionIpState> states) {
        this.states = states;
        return this;
    }
}
