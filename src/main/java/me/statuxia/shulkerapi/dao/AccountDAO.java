package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.DiscordAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface AccountDAO extends JpaRepository<Account, Long> {

    Optional<Account> findByDiscordAccount(DiscordAccount discordAccount);
}
