package me.statuxia.shulkerapi.dto.search.impl;

import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.Account;
import org.springframework.data.domain.Pageable;

public class AccountRoleSearchDTO implements ISearchDTO {

    protected Account account;
    protected boolean active;

    protected Pageable pageable;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    @Override
    public String toString() {
        return "AccountRoleSearchDTO{"
            + "account=" + account
            + ", active=" + active
            + ", pageable=" + pageable
            + '}';
    }
}
