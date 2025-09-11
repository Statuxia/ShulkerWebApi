package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import me.statuxia.shulkerapi.dto.search.impl.BankCardHistorySearchDTO;
import me.statuxia.shulkerapi.model.BankCardHistory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class BankCardHistoryDAO extends BaseDAO<BankCardHistory, Long, BankCardHistorySearchDTO> {

    @Override
    public Predicate buildPredicate(
        CriteriaBuilder cb, Root<BankCardHistory> root, BankCardHistorySearchDTO searchDTO
    ) {
        final List<Predicate> predicates = new ArrayList<>();

        if (!CollectionUtils.isEmpty(searchDTO.getIds())) {
            predicates.add(root.get("id").in(searchDTO.getIds()));
        }

        if (searchDTO.getUuid() != null) {
            predicates.add(cb.equal(root.get("uuid"), searchDTO.getUuid()));
        }

        if (searchDTO.getCard() != null) {
            predicates.add(cb.equal(root.get("card"), searchDTO.getCard()));
        }

        if (!CollectionUtils.isEmpty(searchDTO.getTypes())) {
            predicates.add(root.get("type").in(searchDTO.getTypes()));
        }

        if (searchDTO.getStartCreateTime() != null) {
            predicates.add(cb.equal(root.get("createTime"), searchDTO.getStartCreateTime()));
        }

        if (searchDTO.getEndCreateTime() != null) {
            predicates.add(cb.equal(root.get("createTime"), searchDTO.getEndCreateTime()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<BankCardHistory> getEntityClass() {
        return BankCardHistory.class;
    }
}
