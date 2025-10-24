package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import me.statuxia.shulkerapi.dto.search.impl.FineLogSearchDTO;
import me.statuxia.shulkerapi.model.FineLog;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class FineLogDAO extends BaseDAO<FineLog, Long, FineLogSearchDTO> {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb, Root<FineLog> root, FineLogSearchDTO searchDTO) {
        final List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getFine() == null && CollectionUtils.isEmpty(searchDTO.getFineList())) {
            return cb.disjunction();
        }

        if (searchDTO.getFine() != null) {
            predicates.add(cb.equal(root.get("fine"), searchDTO.getFine()));
        } else {
            predicates.add(root.get("fine").in(searchDTO.getFineList()));
        }

        if (StringUtils.hasText(searchDTO.getActionBy())) {
            predicates.add(cb.equal(root.get("actionBy"), searchDTO.getActionBy()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<FineLog> getEntityClass() {
        return FineLog.class;
    }
}
