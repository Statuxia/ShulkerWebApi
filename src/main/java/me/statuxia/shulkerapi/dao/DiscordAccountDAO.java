package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.DiscordAccount;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface DiscordAccountDAO extends JpaRepository<DiscordAccount, Long> {

    List<DiscordAccount> findByUpdateTimeLessThanEqual(DateTime dateTime, Pageable pageable);

    long countByUpdateTimeLessThanEqual(DateTime dateTime);
}
