package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.AccountDAO;
import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.service.AccountCreateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountCreateServiceImpl implements AccountCreateService {

    private final AccountDAO accountDAO;
    private final DiscordAccountDAO discordAccountDAO;

    @Autowired
    public AccountCreateServiceImpl(AccountDAO accountDAO, DiscordAccountDAO discordAccountDAO) {
        this.accountDAO = accountDAO;
        this.discordAccountDAO = discordAccountDAO;
    }

    /**
     * @return true, если удалось создать аккаунт, false если аккаунт уже создан
     * @throws BaseApiException при провале валидации
     */
    @Override
    @Transactional
    public boolean create(DiscordAccount discordAccount) {
        validateDiscordAccount(discordAccount);

        final Optional<Account> optionalAccount = getAccountDAO().findByDiscordAccount(discordAccount);
        if (optionalAccount.isPresent()) {
            return false;
        }

        final Account account = new Account();
        account.setDiscordAccount(discordAccount);

        getAccountDAO().save(account);
        return true;
    }

    private void validateDiscordAccount(DiscordAccount discordAccount) {
        if (discordAccount == null) {
            throw BaseApiException.INCORRECT_DATA;
        }

        if (!getDiscordAccountDAO().existsById(discordAccount.getId())) {
            throw BaseApiException.INCORRECT_DATA;
        }
    }

    public AccountDAO getAccountDAO() {
        return accountDAO;
    }

    public DiscordAccountDAO getDiscordAccountDAO() {
        return discordAccountDAO;
    }
}
