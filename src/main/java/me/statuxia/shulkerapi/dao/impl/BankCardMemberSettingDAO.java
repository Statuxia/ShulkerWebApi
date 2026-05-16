package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.dto.search.impl.BankCardMemberSettingSearchDTO;
import me.statuxia.shulkerapi.model.BankCardMemberSetting;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class BankCardMemberSettingDAO extends BaseDAO<BankCardMemberSetting, Long, BankCardMemberSettingSearchDTO> {

    @Override
    public Predicate buildPredicate(
        CriteriaBuilder cb, Root<BankCardMemberSetting> root, BankCardMemberSettingSearchDTO searchDTO
    ) {
        final List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getCard() != null) {
            predicates.add(cb.equal(root.get("card"), searchDTO.getCard()));
        }

        if (searchDTO.getBankCardMember() != null) {
            predicates.add(cb.equal(root.get("bankCardMember"), searchDTO.getBankCardMember()));
        }

        if (searchDTO.getType() != null) {
            predicates.add(cb.equal(root.get("type"), searchDTO.getType()));
        }

        if (!CollectionUtils.isEmpty(searchDTO.getTypes())) {
            predicates.add(root.get("type").in(searchDTO.getTypes()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<BankCardMemberSetting> getEntityClass() {
        return BankCardMemberSetting.class;
    }
}
