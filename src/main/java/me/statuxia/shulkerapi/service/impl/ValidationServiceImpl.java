package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.AccountDAO;
import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.Identifiable;
import me.statuxia.shulkerapi.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@Transactional
public class ValidationServiceImpl implements ValidationService {

    private final DiscordAccountDAO discordAccountDAO;
    private final AccountDAO accountDAO;

    @Autowired
    public ValidationServiceImpl(DiscordAccountDAO discordAccountDAO, AccountDAO accountDAO) {
        this.discordAccountDAO = discordAccountDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    public void validateAccount(Account account) {
        validateEntity(getAccountDAO(), account);
    }

    @Override
    public void validateDiscordAccount(DiscordAccount discordAccount) {
        validateEntity(getDiscordAccountDAO(), discordAccount);
    }

    private <T extends Identifiable<O>, O extends Serializable> void validateEntity(
        JpaRepository<T, O> repository, T entity
    ) {
        if (entity == null || entity.getId() == null || !repository.existsById(entity.getId())) {
            throw BaseApiException.INCORRECT_DATA;
        }
    }

    public DiscordAccountDAO getDiscordAccountDAO() {
        return discordAccountDAO;
    }

    public AccountDAO getAccountDAO() {
        return accountDAO;
    }
}
