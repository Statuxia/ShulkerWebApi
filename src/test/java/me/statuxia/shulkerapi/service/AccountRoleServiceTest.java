package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.dao.impl.AccountRoleDAO;
import me.statuxia.shulkerapi.dto.search.impl.AccountRoleSearchDTO;
import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.AccountRole;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.request.AccountRoleRequest;
import me.statuxia.shulkerapi.response.AccountRoleResponseItem;
import me.statuxia.shulkerapi.service.impl.AccountRoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountRoleServiceTest {

    @InjectMocks
    private AccountRoleServiceImpl service;

    @Mock
    private AccountRoleDAO accountRoleDAO;

    @Mock
    private DiscordAccountDAO discordAccountDAO;

    @Test
    void getRolesTest() {
        AccountRoleRequest request = new AccountRoleRequest();
        request.setDiscordIds(List.of(10L, 999L));

        Account account = new Account();
        account.setId(1L);

        DiscordAccount discordAccount = new DiscordAccount();
        discordAccount.setId(10L);
        discordAccount.setAccount(account);

        when(discordAccountDAO.findAllById(List.of(10L, 999L)))
            .thenReturn(List.of(discordAccount));

        AccountRole role1 = new AccountRole();
        role1.setId(100L);

        AccountRole role2 = new AccountRole();
        role2.setId(200L);

        when(accountRoleDAO.findList(any(AccountRoleSearchDTO.class)))
            .thenReturn(List.of(role1, role2));

        List<AccountRoleResponseItem> result = service.getRoles(request);

        assertEquals(2, result.size());

        AccountRoleResponseItem item10 = result.stream()
            .filter(i -> i.getId().equals(10L))
            .findFirst()
            .orElseThrow();
        assertEquals(List.of(100L, 200L), item10.getRoles());

        AccountRoleResponseItem item999 = result.stream()
            .filter(i -> i.getId().equals(999L))
            .findFirst()
            .orElseThrow();
        assertEquals(List.of(), item999.getRoles());

        verify(discordAccountDAO).findAllById(List.of(10L, 999L));
        verify(accountRoleDAO).findList(any());
    }

    @Test
    void getRoleIdsTest() {
        Account account = new Account();
        account.setId(1L);

        DiscordAccount discordAccount = new DiscordAccount();
        discordAccount.setAccount(account);

        AccountRole role = new AccountRole();
        role.setId(500L);

        when(accountRoleDAO.findList(any(AccountRoleSearchDTO.class)))
            .thenReturn(List.of(role));

        List<Long> result = service.getRoleIds(discordAccount);

        assertEquals(List.of(500L), result);

        verify(accountRoleDAO).findList(any(AccountRoleSearchDTO.class));
    }
}

