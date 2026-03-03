package me.statuxia.shulkerapi.dao.impl;

import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.AccountDAO;
import me.statuxia.shulkerapi.dto.search.impl.AccountPropertyDTO;
import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.AccountProperty;
import org.junit.jupiter.api.BeforeEach;
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

@Sql("classpath:sql/AccountPropertyDAOTest.sql")
@SpringBootTest
@Transactional
@TestPropertySource("classpath:test-application.properties")
@DirtiesContext
class AccountPropertyDAOTest extends BaseContainerTest {

    @Autowired
    private AccountPropertyDAO accountPropertyDAO;
    @Autowired
    private AccountDAO accountDAO;

    @BeforeEach
    void setUp() {
        accountPropertyDAO.getEntityManager()
            .createNativeQuery("TRUNCATE TABLE account_property CASCADE")
            .executeUpdate();
        accountPropertyDAO.getEntityManager()
            .createNativeQuery("ALTER SEQUENCE account_property_id_seq RESTART WITH 1")
            .executeUpdate();
    }

    @Test
    void findListByAccountTest() {
        final Account account = accountDAO.findById(1L).get();

        final AccountPropertyDTO dto = new AccountPropertyDTO().setAccount(account);

        final List<AccountProperty> result = accountPropertyDAO.findList(dto);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(p -> p.getAccount().getId().equals(1L)));
    }

    @Test
    void countAllByAccountTest() {
        final Account account = accountDAO.findById(1L).get();

        final AccountPropertyDTO dto = new AccountPropertyDTO().setAccount(account);

        assertEquals(3, accountPropertyDAO.count(dto));
    }

    @Test
    void findListByNameTest() {
        final AccountPropertyDTO dto = new AccountPropertyDTO().setName("SOME_PROPERTY");

        final List<AccountProperty> result = accountPropertyDAO.findList(dto);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(p -> "SOME_PROPERTY".equals(p.getName())));
    }

    @Test
    void findListByAccountAndNameTest() {
        final Account account = accountDAO.findById(1L).get();

        final AccountPropertyDTO dto = new AccountPropertyDTO().setAccount(account).setName("SOME_PROPERTY");

        final List<AccountProperty> result = accountPropertyDAO.findList(dto);

        assertEquals(2, result.size());
    }

    @Test
    void findListByValueTest() {
        final AccountPropertyDTO dto = new AccountPropertyDTO().setValue("value-1");

        final List<AccountProperty> result = accountPropertyDAO.findList(dto);

        assertEquals(1, result.size());
        assertEquals("value-1", result.get(0).getValue());
    }

    @Test
    void findListEmptyPredicateReturnsAllTest() {
        final AccountPropertyDTO dto = new AccountPropertyDTO()
            .setPageable(PageRequest.of(0, 10));

        assertEquals(4, accountPropertyDAO.findList(dto).size());
    }

    @Test
    void saveAndFindByIdTest() {
        final Account account = accountPropertyDAO.getEntityManager().find(Account.class, 1L);

        final AccountProperty property = new AccountProperty();
        property.setAccount(account);
        property.setName("NEW_PROPERTY");
        property.setValue("new-value");

        accountPropertyDAO.save(property);
        assertNotNull(property.getId());

        assertTrue(accountPropertyDAO.findById(property.getId()).isPresent());
        assertEquals("new-value", accountPropertyDAO.findById(property.getId()).get().getValue());
    }

    @Test
    void deleteTest() {
        final long countBefore = accountPropertyDAO.countAll();

        final AccountProperty property = accountPropertyDAO.findById(1L).orElseThrow();
        accountPropertyDAO.forceDelete(property);

        assertEquals(countBefore - 1, accountPropertyDAO.countAll());
        assertTrue(accountPropertyDAO.findById(1L).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("propertyNames")
    void findListByEachNameTest(String name) {
        final AccountPropertyDTO dto = new AccountPropertyDTO().setName(name);

        final List<AccountProperty> result = accountPropertyDAO.findList(dto);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(p -> name.equals(p.getName())));
    }

    static Stream<String> propertyNames() {
        return Stream.of("SOME_PROPERTY", "ANOTHER_PROPERTY");
    }
}