package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.dto.search.impl.BankCardMemberSearchDTO;
import me.statuxia.shulkerapi.model.BankCardMember;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class BankCardMemberDAO extends BaseDAO<BankCardMember, Long, BankCardMemberSearchDTO> {

    @Override
    public Predicate buildPredicate(
        CriteriaBuilder cb, Root<BankCardMember> root, BankCardMemberSearchDTO searchDTO
    ) {
        final List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getCard() != null) {
            predicates.add(cb.equal(root.get("card"), searchDTO.getCard()));
        }

        if (searchDTO.getGameAccount() != null) {
            predicates.add(cb.equal(root.get("gameAccount"), searchDTO.getGameAccount()));
        }

        if (searchDTO.getAddedAtFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("addedAt"), searchDTO.getAddedAtFrom()));
        }

        if (searchDTO.getAddedAtTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("addedAt"), searchDTO.getAddedAtTo()));
        }

        if (searchDTO.getCreditedFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("credited"), searchDTO.getCreditedFrom()));
        }

        if (searchDTO.getCreditedTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("credited"), searchDTO.getCreditedTo()));
        }

        if (searchDTO.getDebitedFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("debited"), searchDTO.getDebitedFrom()));
        }

        if (searchDTO.getDebitedTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("debited"), searchDTO.getDebitedTo()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<BankCardMember> getEntityClass() {
        return BankCardMember.class;
    }
}
