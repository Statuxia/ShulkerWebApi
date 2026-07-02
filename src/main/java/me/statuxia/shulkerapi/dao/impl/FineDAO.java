package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import me.statuxia.shulkerapi.dto.search.impl.FineSearchDTO;
import me.statuxia.shulkerapi.model.Fine;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class FineDAO extends BaseDAO<Fine, Long, FineSearchDTO> {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb, Root<Fine> root, FineSearchDTO searchDTO) {
        final List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getGameAccount() != null) {
            predicates.add(cb.equal(root.get("gameAccount"), searchDTO.getGameAccount()));
        }

        if (!CollectionUtils.isEmpty(searchDTO.getStatuses())) {
            predicates.add(root.get("status").in(searchDTO.getStatuses()));
        }

        if (StringUtils.hasText(searchDTO.getActionBy())) {
            predicates.add(cb.equal(root.get("actionBy"), searchDTO.getActionBy()));
        }

        if (searchDTO.isUnnotifiedOnly()) {
            predicates.add(cb.equal(root.get("notified"), false));
        }

        if (searchDTO.getDueDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), searchDTO.getDueDateFrom()));
        }

        if (searchDTO.getDueDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), searchDTO.getDueDateTo()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<Fine> getEntityClass() {
        return Fine.class;
    }
}
