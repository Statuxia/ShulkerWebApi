package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dao.RoleDAO;
import me.statuxia.shulkerapi.model.Role;
import me.statuxia.shulkerapi.response.RoleResponseItem;
import me.statuxia.shulkerapi.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl service;

    @Mock
    private RoleDAO roleDAO;

    @Test
    void getAllRolesTest() {
        Role role = new Role();
        role.setId(1L);
        role.setRoleId("ADMIN");
        role.setName("Administrator");
        role.setColor("#FF0000");
        role.setDiscordId(999L);
        role.setLuckpermsPermission("perm.admin");
        role.setAvailableForTwink(true);

        when(roleDAO.findAll()).thenReturn(List.of(role));

        List<RoleResponseItem> result = service.getAllRoles();

        assertEquals(1, result.size());

        RoleResponseItem item = result.getFirst();

        assertEquals(1L, (long) item.getId());
        assertEquals("ADMIN", item.getRoleId());
        assertEquals("Administrator", item.getName());
        assertEquals("#FF0000", item.getColor());
        assertEquals(999L, (long) item.getDiscordId());
        assertEquals("perm.admin", item.getLuckpermsPermission());
        assertTrue(item.getAvailableForTwink());

        verify(roleDAO).findAll();
    }
}

