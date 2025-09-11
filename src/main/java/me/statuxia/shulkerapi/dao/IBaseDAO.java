package me.statuxia.shulkerapi.dao;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.Identifiable;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IBaseDAO<E extends Identifiable<I>, I extends Serializable, T extends ISearchDTO> {

    Long count(T searchDTO);

    List<E> findList(T searchDTO);

    Optional<E> find(T searchDTO);

    Optional<E> findById(I id);

    void save(E entity);

    void saveAll(Collection<E> entities);

    Predicate buildPredicate(CriteriaBuilder cb, Root<E> root, T searchDTO);

    void order(CriteriaQuery<E> query, CriteriaBuilder cb, Root<E> root, Pageable pageable);

    TypedQuery<E> applyPageable(TypedQuery<E> query, Pageable pageable);

    Class<E> getEntityClass();
}
