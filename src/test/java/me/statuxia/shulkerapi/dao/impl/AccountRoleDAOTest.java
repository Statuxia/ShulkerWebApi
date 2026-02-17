package me.statuxia.shulkerapi.dao.impl;

import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dto.search.impl.AccountRoleSearchDTO;
import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.AccountRole;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql({
    "classpath:sql/AccountRoleDAOTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DirtiesContext
@Transactional
class AccountRoleDAOTest extends BaseContainerTest {

    @Autowired
    private AccountRoleDAO accountRoleDAO;

    private Account account(Long id) {
        Account a = new Account();
        a.setId(id);
        return a;
    }

    @Test
    void search_nullAccount_returnsEmpty() {
        AccountRoleSearchDTO dto = new AccountRoleSearchDTO();

        List<AccountRole> result = accountRoleDAO.findList(dto);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_accountNotExists_returnsEmpty() {
        AccountRoleSearchDTO dto = new AccountRoleSearchDTO();
        dto.setAccount(account(999L));

        List<AccountRole> result = accountRoleDAO.findList(dto);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_account1_returns4Roles() {
        AccountRoleSearchDTO dto = new AccountRoleSearchDTO();
        dto.setAccount(account(1L));

        List<AccountRole> result = accountRoleDAO.findList(dto);

        assertEquals(4, result.size());
    }

    @Test
    void search_account2_returns3Roles() {
        AccountRoleSearchDTO dto = new AccountRoleSearchDTO();
        dto.setAccount(account(2L));

        List<AccountRole> result = accountRoleDAO.findList(dto);

        assertEquals(3, result.size());
    }

    @Test
    void search_account1_activeTrue_returnsOnlyNotExpired() {
        AccountRoleSearchDTO dto = new AccountRoleSearchDTO();
        dto.setAccount(account(1L));
        dto.setActive(true);

        List<AccountRole> result = accountRoleDAO.findList(dto);

        assertEquals(2, result.size());

        result.forEach(role ->
            assertTrue(role.getExpireDate().isAfterNow()
                || role.getExpireDate().isEqual(DateTime.now()))
        );
    }

    @Test
    void search_account1_activeFalse_returnsAll() {
        AccountRoleSearchDTO dto = new AccountRoleSearchDTO();
        dto.setAccount(account(1L));
        dto.setActive(false);

        List<AccountRole> result = accountRoleDAO.findList(dto);

        assertEquals(4, result.size());
    }
}
