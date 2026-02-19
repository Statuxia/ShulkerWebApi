package me.statuxia.shulkerapi.service.impl;

import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.dao.impl.AccountRoleDAO;
import me.statuxia.shulkerapi.dto.search.impl.AccountRoleSearchDTO;
import me.statuxia.shulkerapi.model.AccountRole;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.Identifiable;
import me.statuxia.shulkerapi.request.AccountRoleRequest;
import me.statuxia.shulkerapi.response.AccountRoleResponseItem;
import me.statuxia.shulkerapi.service.AccountRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AccountRoleServiceImpl implements AccountRoleService {

    private final AccountRoleDAO accountRoleDAO;
    private final DiscordAccountDAO discordAccountDAO;

    @Autowired
    public AccountRoleServiceImpl(
        AccountRoleDAO accountRoleDAO,
        DiscordAccountDAO discordAccountDAO
    ) {
        this.accountRoleDAO = accountRoleDAO;
        this.discordAccountDAO = discordAccountDAO;
    }

    public List<AccountRoleResponseItem> getRoles(AccountRoleRequest request) {
        final List<DiscordAccount> allById = discordAccountDAO.findAllById(request.getDiscordIds());

        return allById.stream().map(discordAccount -> {
            final AccountRoleResponseItem item = new AccountRoleResponseItem();
            return item
                .setRoles(getRoleIds(discordAccount))
                .setId(discordAccount.getId());
        }).toList();
    }

    public List<Long> getRoleIds(DiscordAccount discordAccount) {
        final AccountRoleSearchDTO dto = new AccountRoleSearchDTO();
        dto.setAccount(discordAccount.getAccount());
        dto.setActive(true);
        final List<AccountRole> list = accountRoleDAO.findList(dto);
        return list.stream().map(Identifiable::getId).toList();
    }
}
