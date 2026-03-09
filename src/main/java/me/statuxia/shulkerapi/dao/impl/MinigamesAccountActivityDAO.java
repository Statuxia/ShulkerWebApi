package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.dto.search.impl.MinigamesAccountActivityDTO;
import me.statuxia.shulkerapi.model.MinigamesAccountActivity;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class MinigamesAccountActivityDAO extends BaseDAO<MinigamesAccountActivity, Long, MinigamesAccountActivityDTO> {

    @Override
    public Predicate buildPredicate(
        CriteriaBuilder cb, Root<MinigamesAccountActivity> root, MinigamesAccountActivityDTO searchDTO
    ) {
        final List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getGameAccount() != null) {
            predicates.add(cb.equal(root.get("gameAccount"), searchDTO.getGameAccount()));
        }

        if (searchDTO.getType() != null) {
            predicates.add(cb.equal(root.get("type"), searchDTO.getType()));
        }

        if (!CollectionUtils.isEmpty(searchDTO.getTypes())) {
            predicates.add(root.get("type").in(searchDTO.getTypes()));
        }

        if (searchDTO.getActivityAtFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("activityAt"), searchDTO.getActivityAtFrom()));
        }

        if (searchDTO.getActivityAtTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("activityAt"), searchDTO.getActivityAtTo()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<MinigamesAccountActivity> getEntityClass() {
        return MinigamesAccountActivity.class;
    }
}
