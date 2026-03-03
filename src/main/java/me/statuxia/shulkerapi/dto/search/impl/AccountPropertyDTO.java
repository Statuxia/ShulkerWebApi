package me.statuxia.shulkerapi.dto.search.impl;


import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.Account;
import org.springframework.data.domain.Pageable;

public class AccountPropertyDTO implements ISearchDTO {

    protected Account account;
    protected String name;
    protected String value;

    protected Pageable pageable;

    public Account getAccount() {
        return account;
    }

    public AccountPropertyDTO setAccount(Account account) {
        this.account = account;
        return this;
    }

    public String getName() {
        return name;
    }

    public AccountPropertyDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public AccountPropertyDTO setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    public AccountPropertyDTO setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }
}