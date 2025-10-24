package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import me.statuxia.shulkerapi.aware.GameAccountAware;
import me.statuxia.shulkerapi.model.FineStatus;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FineListRequest extends PaginationRequest implements GameAccountAware {

    @NotNull
    @NotEmpty
    protected String gameAccount;
    protected List<FineStatus> statuses;
    protected String actionBy;
    protected boolean unnotifiedOnly;

    @Override
    public String getGameAccount() {
        return gameAccount;
    }

    @Override
    public void setGameAccount(String gameAccount) {
        this.gameAccount = gameAccount;
    }

    public List<FineStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<FineStatus> statuses) {
        this.statuses = statuses;
    }

    public String getActionBy() {
        return actionBy;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }

    public boolean isUnnotifiedOnly() {
        return unnotifiedOnly;
    }

    public void setUnnotifiedOnly(boolean unnotifiedOnly) {
        this.unnotifiedOnly = unnotifiedOnly;
    }
}
