package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.PaidAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PaidAccountDAO extends JpaRepository<PaidAccount, Long> {

    List<PaidAccount> findByNameIgnoreCase(String name);
}
