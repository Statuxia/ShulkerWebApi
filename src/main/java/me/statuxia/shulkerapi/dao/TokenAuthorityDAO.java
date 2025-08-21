package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.TokenAuthority;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.model.TokenAuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface TokenAuthorityDAO extends JpaRepository<TokenAuthority, TokenAuthorityId> {

    List<TokenAuthority> findByToken(String token);

    boolean hasAuthorityByIdAndAuthority(String id, TokenAuthorityEnum authority);
}
