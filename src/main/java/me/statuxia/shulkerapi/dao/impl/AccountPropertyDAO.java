package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.dto.search.impl.AccountPropertyDTO;
import me.statuxia.shulkerapi.model.AccountProperty;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class AccountPropertyDAO extends BaseDAO<AccountProperty, Long, AccountPropertyDTO> {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb, Root<AccountProperty> root, AccountPropertyDTO searchDTO) {
        final List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getAccount() != null) {
            predicates.add(cb.equal(root.get("account"), searchDTO.getAccount()));
        }

        if (StringUtils.hasText(searchDTO.getName())) {
            predicates.add(cb.equal(root.get("name"), searchDTO.getName()));
        }

        if (StringUtils.hasText(searchDTO.getValue())) {
            predicates.add(cb.equal(root.get("value"), searchDTO.getValue()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<AccountProperty> getEntityClass() {
        return AccountProperty.class;
    }
}
