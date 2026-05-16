package me.statuxia.shulkerapi.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import me.statuxia.shulkerapi.dto.search.impl.GameSessionIpDTO;
import me.statuxia.shulkerapi.model.GameSessionIp;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class GameSessionIpDAO extends BaseDAO<GameSessionIp, Long, GameSessionIpDTO> {

    private final ApplicationContext context;

    public GameSessionIpDAO(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb, Root<GameSessionIp> root, GameSessionIpDTO searchDTO) {
        final List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(searchDTO.getIp())) {
            predicates.add(cb.equal(root.get("ip"), searchDTO.getIp()));
        }

        if (searchDTO.getGameAccount() != null) {
            predicates.add(cb.equal(root.get("gameAccount"), searchDTO.getGameAccount()));
        }

        if (searchDTO.getLastJoinDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("lastJoinDate"), searchDTO.getLastJoinDateFrom()));
        }

        if (searchDTO.getLastJoinDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("lastJoinDate"), searchDTO.getLastJoinDateTo()));
        }

        if (!CollectionUtils.isEmpty(searchDTO.getStates())) {
            predicates.add(root.get("state").in(searchDTO.getStates()));
        }

        if (searchDTO.isNotified() != null) {
            predicates.add(cb.equal(root.get("notified"), searchDTO.isNotified()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Class<GameSessionIp> getEntityClass() {
        return GameSessionIp.class;
    }

    @Override
    public void save(GameSessionIp entity) {
        super.save(entity);
        context.getBean(GameSessionIpDAO.class).evictCache(entity.getGameAccount().getName(), entity.getIp());
    }

    @CacheEvict(value = "auth_validate", key = "#name.toLowerCase() + '_' + #ip")
    public void evictCache(String name, String ip) {
    }
}
