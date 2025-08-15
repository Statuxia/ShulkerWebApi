package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.TokenAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TokenAuthorityDAO extends JpaRepository<TokenAuthority, String> {
}
