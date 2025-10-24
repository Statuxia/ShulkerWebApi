package me.statuxia.shulkerapi.dao.impl;

import me.statuxia.shulkerapi.model.CardStyle;
import me.statuxia.shulkerapi.model.CardStyleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CardStyleDAO extends JpaRepository<CardStyle, CardStyleType> {
}
