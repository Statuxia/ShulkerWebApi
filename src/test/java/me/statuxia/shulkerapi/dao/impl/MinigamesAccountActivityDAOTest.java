package me.statuxia.shulkerapi.dao.impl;

import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dto.search.impl.MinigamesAccountActivityDTO;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.MinigamesAccountActivity;
import me.statuxia.shulkerapi.model.MinigamesAccountActivityType;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Sql("classpath:sql/MinigamesAccountActivityDAOTest.sql")
@SpringBootTest
@Transactional
@TestPropertySource("classpath:test-application.properties")
@DirtiesContext
class MinigamesAccountActivityDAOTest extends BaseContainerTest {

    @Autowired
    private MinigamesAccountActivityDAO minigamesAccountActivityDAO;
    @Autowired
    private GameAccountDAO gameAccountDAO;

    @Test
    void findListByGameAccountTest() {
        final GameAccount gameAccount = gameAccountDAO.findById(1L).get();

        final MinigamesAccountActivityDTO dto = new MinigamesAccountActivityDTO().setGameAccount(gameAccount);

        final List<MinigamesAccountActivity> result = minigamesAccountActivityDAO.findList(dto);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(a -> a.getGameAccount().getId().equals(1L)));
    }

    @Test
    void countAllByGameAccountTest() {
        final GameAccount gameAccount = gameAccountDAO.findById(1L).get();

        final MinigamesAccountActivityDTO dto = new MinigamesAccountActivityDTO().setGameAccount(gameAccount);

        assertEquals(3, minigamesAccountActivityDAO.count(dto));
    }

    @Test
    void findListByTypeTest() {
        final MinigamesAccountActivityDTO dto = new MinigamesAccountActivityDTO()
            .setType(MinigamesAccountActivityType.JOIN_SERVER);

        final List<MinigamesAccountActivity> result = minigamesAccountActivityDAO.findList(dto);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(a -> MinigamesAccountActivityType.JOIN_SERVER.equals(a.getType())));
    }

    @Test
    void findListByGameAccountAndTypeTest() {
        final GameAccount gameAccount = gameAccountDAO.findById(1L).get();

        final MinigamesAccountActivityDTO dto = new MinigamesAccountActivityDTO()
            .setGameAccount(gameAccount)
            .setType(MinigamesAccountActivityType.JOIN_SERVER);

        final List<MinigamesAccountActivity> result = minigamesAccountActivityDAO.findList(dto);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getGameAccount().getId().equals(1L)));
        assertTrue(result.stream().allMatch(a -> MinigamesAccountActivityType.JOIN_SERVER.equals(a.getType())));
    }

    @Test
    void findListByActivityAtFromTest() {
        final DateTime from = new DateTime(2026, 1, 2, 0, 0, 0);

        final MinigamesAccountActivityDTO dto = new MinigamesAccountActivityDTO().setActivityAtFrom(from);

        final List<MinigamesAccountActivity> result = minigamesAccountActivityDAO.findList(dto);

        assertEquals(3, result.size());
    }

    @Test
    void findListByActivityAtToTest() {
        final DateTime to = new DateTime(2026, 1, 2, 23, 59, 59);

        final MinigamesAccountActivityDTO dto = new MinigamesAccountActivityDTO().setActivityAtTo(to);

        final List<MinigamesAccountActivity> result = minigamesAccountActivityDAO.findList(dto);

        assertEquals(2, result.size());
    }

    @Test
    void findListByActivityAtRangeTest() {
        final DateTime from = new DateTime(2026, 1, 2, 0, 0, 0);
        final DateTime to = new DateTime(2026, 1, 3, 23, 59, 59);

        final MinigamesAccountActivityDTO dto = new MinigamesAccountActivityDTO()
            .setActivityAtFrom(from)
            .setActivityAtTo(to);

        final List<MinigamesAccountActivity> result = minigamesAccountActivityDAO.findList(dto);

        assertEquals(2, result.size());
    }

    @Test
    void findListByTypesTest() {
        final MinigamesAccountActivityDTO dto = new MinigamesAccountActivityDTO()
            .setTypes(List.of(MinigamesAccountActivityType.JOIN_SERVER, MinigamesAccountActivityType.LEFT_SERVER));

        final List<MinigamesAccountActivity> result = minigamesAccountActivityDAO.findList(dto);

        assertEquals(4, result.size());
    }

    @Test
    void findListByTypesFiltersSingleTest() {
        final MinigamesAccountActivityDTO dto = new MinigamesAccountActivityDTO()
            .setTypes(List.of(MinigamesAccountActivityType.LEFT_SERVER));

        final List<MinigamesAccountActivity> result = minigamesAccountActivityDAO.findList(dto);

        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(a -> MinigamesAccountActivityType.LEFT_SERVER.equals(a.getType())));
    }

    @Test
    void findListEmptyPredicateReturnsAllTest() {
        final MinigamesAccountActivityDTO dto = new MinigamesAccountActivityDTO()
            .setPageable(PageRequest.of(0, 10));

        assertEquals(4, minigamesAccountActivityDAO.findList(dto).size());
    }

    @Test
    void saveAndFindByIdTest() {
        final GameAccount gameAccount = minigamesAccountActivityDAO.getEntityManager().find(GameAccount.class, 1L);

        final MinigamesAccountActivity activity = new MinigamesAccountActivity();
        activity.setGameAccount(gameAccount);
        activity.setActivityAt(new DateTime(2026, 6, 1, 9, 0, 0));
        activity.setType(MinigamesAccountActivityType.JOIN_SERVER);

        minigamesAccountActivityDAO.save(activity);
        assertNotNull(activity.getId());

        assertTrue(minigamesAccountActivityDAO.findById(activity.getId()).isPresent());
        assertEquals(
            MinigamesAccountActivityType.JOIN_SERVER,
            minigamesAccountActivityDAO.findById(activity.getId()).get().getType()
        );
    }

    @Test
    void deleteTest() {
        final long countBefore = minigamesAccountActivityDAO.countAll();

        final MinigamesAccountActivity activity = minigamesAccountActivityDAO.findById(1L).orElseThrow();
        minigamesAccountActivityDAO.forceDelete(activity);

        assertEquals(countBefore - 1, minigamesAccountActivityDAO.countAll());
        assertTrue(minigamesAccountActivityDAO.findById(1L).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("activityTypes")
    void findListByEachTypeTest(MinigamesAccountActivityType type) {
        final MinigamesAccountActivityDTO dto = new MinigamesAccountActivityDTO().setType(type);

        final List<MinigamesAccountActivity> result = minigamesAccountActivityDAO.findList(dto);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(a -> type.equals(a.getType())));
    }

    static Stream<MinigamesAccountActivityType> activityTypes() {
        return Stream.of(MinigamesAccountActivityType.JOIN_SERVER, MinigamesAccountActivityType.LEFT_SERVER);
    }
}