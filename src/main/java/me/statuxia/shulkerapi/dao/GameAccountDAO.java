package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.GameAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface GameAccountDAO extends JpaRepository<GameAccount, Long> {

    Optional<GameAccount> findByName(String name);

    List<GameAccount> findByDiscordAccount(DiscordAccount discordAccount);

    List<GameAccount> findByNameIgnoreCase(String name);
}
