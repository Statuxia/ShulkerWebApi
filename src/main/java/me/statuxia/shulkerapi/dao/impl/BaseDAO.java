package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import me.statuxia.shulkerapi.dao.IBaseDAO;
import me.statuxia.shulkerapi.dto.search.ISearchDTO;
import me.statuxia.shulkerapi.model.Identifiable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public abstract class BaseDAO<E extends Identifiable<I>, I extends Serializable, T extends ISearchDTO>
    implements IBaseDAO<E, I, T> {

    private final BaseDAO<E, I, T> dao;
    @PersistenceContext
    private EntityManager entityManager;

    protected BaseDAO() {
        this.dao = this;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Optional<E> findById(I id) {
        return Optional.ofNullable(getEntityManager().find(getEntityClass(), id));
    }

    @Override
    public Long count(T searchDTO) {
        final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        final Root<E> root = query.from(getEntityClass());

        final Predicate predicate = buildPredicate(cb, root, searchDTO);

        query.select(cb.count(root)).where(predicate);
        return getEntityManager().createQuery(query).getSingleResult();
    }

    @Override
    public List<E> findList(T searchDTO) {
        final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        final CriteriaQuery<E> query = cb.createQuery(getEntityClass());
        final Root<E> root = query.from(getEntityClass());

        final Predicate predicate = buildPredicate(cb, root, searchDTO);

        query.where(predicate);
        order(query, cb, root, searchDTO.getPageable());

        return applyPageable(getEntityManager().createQuery(query), searchDTO.getPageable()).getResultList();
    }

    @Override
    public Optional<E> find(T searchDTO) {
        return dao.findList(searchDTO).stream().findFirst();
    }

    @Override
    public List<E> findAll() {
        final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        final CriteriaQuery<E> query = cb.createQuery(getEntityClass());

        final Root<E> root = query.from(getEntityClass());
        query.select(root);

        return getEntityManager().createQuery(query).getResultList();
    }

    @Override
    public Long countAll() {
        final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        final Root<E> root = query.from(getEntityClass());

        query.select(cb.count(root));
        return getEntityManager().createQuery(query).getSingleResult();
    }

    @Override
    public void save(E entity) {
        if (entity.getId() != null) {
            getEntityManager().merge(entity);
            return;
        }
        getEntityManager().persist(entity);
    }

    @Override
    public void saveAll(Collection<E> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        for (E entity : entities) {
            save(entity);
        }
    }

    @Override
    public void forceDelete(E entity) {
        getEntityManager().remove(entity);
    }

    @Override
    public void order(CriteriaQuery<E> query, CriteriaBuilder cb, Root<E> root, Pageable pageable) {
        if (pageable == null) {
            return;
        }

        pageable.getSort().stream().findFirst().ifPresent(o -> {
            final Order order;
            if (o.isAscending()) {
                order = cb.asc(root.get(o.getProperty()));
            } else {
                order = cb.desc(root.get(o.getProperty()));
            }

            query.orderBy(order);
        });
    }

    @Override
    public TypedQuery<E> applyPageable(TypedQuery<E> query, Pageable pageable) {
        if (pageable == null) {
            return query;
        }

        return query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
    }
}
