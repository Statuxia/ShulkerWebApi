package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.MinigamesAccountActivityType;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class MinigamesAccountActivityDTO implements ISearchDTO {

    protected GameAccount gameAccount;
    protected DateTime activityAtFrom;
    protected DateTime activityAtTo;
    protected MinigamesAccountActivityType type;
    protected List<MinigamesAccountActivityType> types;

    protected Pageable pageable;

    public GameAccount getGameAccount() {
        return gameAccount;
    }

    public MinigamesAccountActivityDTO setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }

    public DateTime getActivityAtFrom() {
        return activityAtFrom;
    }

    public MinigamesAccountActivityDTO setActivityAtFrom(DateTime activityAtFrom) {
        this.activityAtFrom = activityAtFrom;
        return this;
    }

    public DateTime getActivityAtTo() {
        return activityAtTo;
    }

    public MinigamesAccountActivityDTO setActivityAtTo(DateTime activityAtTo) {
        this.activityAtTo = activityAtTo;
        return this;
    }

    public MinigamesAccountActivityType getType() {
        return type;
    }

    public MinigamesAccountActivityDTO setType(MinigamesAccountActivityType type) {
        this.type = type;
        return this;
    }

    public List<MinigamesAccountActivityType> getTypes() {
        return types;
    }

    public MinigamesAccountActivityDTO setTypes(
        List<MinigamesAccountActivityType> types
    ) {
        this.types = types;
        return this;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public MinigamesAccountActivityDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }
}
