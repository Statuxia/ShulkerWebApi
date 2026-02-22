package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.request.AccountRoleRequest;
import me.statuxia.shulkerapi.response.AccountRoleResponseItem;

import java.util.List;

public interface AccountRoleService {

    List<AccountRoleResponseItem> getRoles(AccountRoleRequest request);

    List<Long> getRoleIds(DiscordAccount discordAccount);
}
