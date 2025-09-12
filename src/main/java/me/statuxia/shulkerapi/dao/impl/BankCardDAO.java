package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import me.statuxia.shulkerapi.dto.search.impl.BankCardSearchDTO;
import me.statuxia.shulkerapi.model.BankCard;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class BankCardDAO extends BaseDAO<BankCard, Long, BankCardSearchDTO> {

    private final BankCardDAO bankCardDAO;

    public BankCardDAO() {
        this.bankCardDAO = this;
    }

    public Optional<BankCard> findByNumber(String number) {
        if (!StringUtils.hasText(number)) {
            return Optional.empty();
        }

        final BankCardSearchDTO dto = new BankCardSearchDTO();
        dto.setNumber(number);
        return bankCardDAO.findList(dto).stream().findFirst();
    }

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb, Root<BankCard> root, BankCardSearchDTO searchDTO) {
        final List<Predicate> predicates = new ArrayList<>();

        if (!StringUtils.hasText(searchDTO.getNumber()) && searchDTO.getGameAccount() == null) {
            return cb.disjunction();
        }

        if (StringUtils.hasText(searchDTO.getNumber())) {
            predicates.add(cb.equal(root.get("number"), searchDTO.getNumber()));
        }

        if (searchDTO.getCardType() != null) {
            predicates.add(cb.equal(root.get("type"), searchDTO.getCardType()));
        }

        if (searchDTO.getGameAccount() != null) {
            predicates.add(cb.equal(root.get("gameAccount"), searchDTO.getGameAccount()));
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
    public Class<BankCard> getEntityClass() {
        return BankCard.class;
    }
}
