package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.AccountDAO;
import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.service.AccountCreateService;
import me.statuxia.shulkerapi.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountCreateServiceImpl implements AccountCreateService {

    private final AccountDAO accountDAO;
    private final DiscordAccountDAO discordAccountDAO;
    private final ValidationService validationService;

    @Autowired
    public AccountCreateServiceImpl(
        AccountDAO accountDAO,
        DiscordAccountDAO discordAccountDAO,
        ValidationService validationService
    ) {
        this.accountDAO = accountDAO;
        this.discordAccountDAO = discordAccountDAO;
        this.validationService = validationService;
    }

    /**
     * @return true, если удалось создать аккаунт, false если аккаунт уже создан
     * @throws BaseApiException при провале валидации
     */
    @Override
    @Transactional
    public Account create(DiscordAccount discordAccount) {
        validationService.validateDiscordAccount(discordAccount);

        final Account account = getAccountDAO().findByDiscordAccount(discordAccount).orElseGet(() -> {
            final Account newAccount = new Account();
            newAccount.setDiscordAccount(discordAccount);

            getAccountDAO().save(newAccount);

            return newAccount;
        });

        discordAccount.setAccount(account);
        return account;
    }

    public AccountDAO getAccountDAO() {
        return accountDAO;
    }

    public DiscordAccountDAO getDiscordAccountDAO() {
        return discordAccountDAO;
    }
}
