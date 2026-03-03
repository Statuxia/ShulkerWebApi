package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import me.statuxia.shulkerapi.dto.search.impl.GameAccountBalanceSearchDTO;
import me.statuxia.shulkerapi.model.GameAccountBalance;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class GameAccountBalanceDAO extends BaseDAO<GameAccountBalance, Long, GameAccountBalanceSearchDTO> {

    @Override
    public Predicate buildPredicate(
        CriteriaBuilder cb, Root<GameAccountBalance> root,
        GameAccountBalanceSearchDTO searchDTO
    ) {
        final List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getGameAccount() == null || searchDTO.getType() == null) {
            return cb.disjunction();
        }

        predicates.add(cb.equal(root.get("gameAccount"), searchDTO.getGameAccount()));
        predicates.add(cb.equal(root.get("type"), searchDTO.getType()));

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<GameAccountBalance> getEntityClass() {
        return GameAccountBalance.class;
    }
}
