package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.TokenLimitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TokenLimitationDAO extends JpaRepository<TokenLimitation, String> {
}
