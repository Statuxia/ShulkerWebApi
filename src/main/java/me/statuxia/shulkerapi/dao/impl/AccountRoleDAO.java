package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.dto.search.impl.AccountRoleSearchDTO;
import me.statuxia.shulkerapi.model.AccountRole;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
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

        if (searchDTO.isActive()) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("expireDate"), DateTime.now()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<AccountRole> getEntityClass() {
        return AccountRole.class;
    }
}
