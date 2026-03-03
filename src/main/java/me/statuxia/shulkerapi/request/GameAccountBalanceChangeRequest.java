package me.statuxia.shulkerapi.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import me.statuxia.shulkerapi.aware.GameAccountAware;
import me.statuxia.shulkerapi.model.GameAccountBalanceType;
import org.springframework.lang.NonNull;

public class GameAccountBalanceChangeRequest implements GameAccountAware {

    @NotNull
    @NotEmpty
    protected String gameAccount;

    @NonNull
    protected GameAccountBalanceType type;

    @NonNull
    protected Long value;

    @Override
    public String getGameAccount() {
        return gameAccount;
    }

    @Override
    public void setGameAccount(String gameAccount) {
        this.gameAccount = gameAccount;
    }

    @NonNull
    public GameAccountBalanceType getType() {
        return type;
    }

    public void setType(@NonNull GameAccountBalanceType type) {
        this.type = type;
    }

    @NonNull
    public Long getValue() {
        return value;
    }

    public GameAccountBalanceChangeRequest setValue(@NonNull Long value) {
        this.value = value;
        return this;
    }
}
