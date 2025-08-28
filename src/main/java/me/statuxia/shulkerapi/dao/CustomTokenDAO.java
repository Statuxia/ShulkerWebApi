package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.CustomToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface CustomTokenDAO extends JpaRepository<CustomToken, Long> {

    Optional<CustomToken> findByToken(String token);
}
