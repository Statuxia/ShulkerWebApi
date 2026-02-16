package me.statuxia.shulkerapi.service.impl;

import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.dao.RoleDAO;
import me.statuxia.shulkerapi.response.RoleResponseItem;
import me.statuxia.shulkerapi.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleDAO roleDAO;

    @Autowired
    public RoleServiceImpl(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @Override
    public List<RoleResponseItem> getAllRoles() {
        return roleDAO.findAll().stream()
            .map(role -> new RoleResponseItem()
                .setId(role.getId())
                .setRoleId(role.getRoleId())
                .setColor(role.getColor())
                .setDiscordId(role.getDiscordId())
                .setName(role.getName())
                .setLuckpermsPermission(role.getLuckpermsPermission())
                .setAvailableForTwink(role.getAvailableForTwink())
            )
            .toList();
    }
}
