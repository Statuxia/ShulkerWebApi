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
import java.util.Map;
import java.util.stream.Collectors;

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
        if (request.getDiscordIds() == null || request.getDiscordIds().isEmpty()) {
            return List.of();
        }

        final List<Long> uniqueIds = request.getDiscordIds().stream()
            .distinct()
            .toList();

        final List<DiscordAccount> accounts = discordAccountDAO.findAllById(uniqueIds);

        final Map<Long, DiscordAccount> accountMap = accounts.stream()
            .collect(Collectors.toMap(DiscordAccount::getId, a -> a));

        return uniqueIds.stream()
            .map(id -> {
                final AccountRoleResponseItem item = new AccountRoleResponseItem();
                item.setId(id);

                final DiscordAccount account = accountMap.get(id);
                if (account != null) {
                    item.setRoles(getRoleIds(account));
                } else {
                    item.setRoles(List.of());
                }

                return item;
            })
            .toList();
    }

    public List<Long> getRoleIds(DiscordAccount discordAccount) {
        final AccountRoleSearchDTO dto = new AccountRoleSearchDTO();
        dto.setAccount(discordAccount.getAccount());
        dto.setActive(true);
        final List<AccountRole> list = accountRoleDAO.findList(dto);
        return list.stream().map(Identifiable::getId).toList();
    }
}
