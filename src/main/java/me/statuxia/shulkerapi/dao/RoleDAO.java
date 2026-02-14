package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RoleDAO extends JpaRepository<Role, Long> {
}
