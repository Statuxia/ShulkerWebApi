package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.dto.search.impl.QueueScheduleSearchDTO;
import me.statuxia.shulkerapi.model.QueueSchedule;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class QueueScheduleDAO extends BaseDAO<QueueSchedule, Long, QueueScheduleSearchDTO> {

    @Override
    public Predicate buildPredicate(
        CriteriaBuilder cb,
        Root<QueueSchedule> root,
        QueueScheduleSearchDTO searchDTO
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getType() != null) {
            predicates.add(cb.equal(root.get("type"), searchDTO.getType()));
        }

        if (searchDTO.isCompleted() != null) {
            predicates.add(cb.equal(root.get("completed"), searchDTO.isCompleted()));
        }

        if (searchDTO.getCreatedAfter() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createDate"), searchDTO.getCreatedAfter()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<QueueSchedule> getEntityClass() {
        return QueueSchedule.class;
    }
}