package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface SessionTokenDAO extends JpaRepository<SessionToken, Long> {

    Optional<SessionToken> findByToken(String token);
}
