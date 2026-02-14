package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import me.statuxia.shulkerapi.dto.search.impl.AccountRoleSearchDTO;
import me.statuxia.shulkerapi.model.AccountRole;

import java.util.ArrayList;
import java.util.List;

public class AccountRoleDAO extends BaseDAO<AccountRole, Long, AccountRoleSearchDTO> {
    @Override
    public Predicate buildPredicate(
        CriteriaBuilder cb,
        Root<AccountRole> root,
        AccountRoleSearchDTO searchDTO
    ) {
        final List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getAccount() == null) {
            return cb.disjunction();
        }

        predicates.add(cb.equal(root.get("account"), searchDTO.getAccount()));

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<AccountRole> getEntityClass() {
        return AccountRole.class;
    }
}
