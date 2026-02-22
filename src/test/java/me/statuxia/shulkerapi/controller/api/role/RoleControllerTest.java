package me.statuxia.shulkerapi.controller.api.role;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.response.RoleResponseItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
    "classpath:sql/RoleControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
class RoleControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listTest() throws Exception {
        MvcResult result = mockMvc.perform(
                get(RoleController.PREFIX + RoleController.LIST)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
            ).andExpect(status().isOk())
            .andReturn();

        List<RoleResponseItem> roles =
            objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
            );

        assertFalse(roles.isEmpty());
        assertEquals(
            List.of(
                new RoleResponseItem()
                    .setId(1L)
                    .setRoleId("discord-role-1")
                    .setName("Admin")
                    .setColor("#FF0000")
                    .setDiscordId(1L)
                    .setLuckpermsPermission("group.admin")
                    .setAvailableForTwink(true),
                new RoleResponseItem().setId(2L)
                    .setRoleId("discord-role-2")
                    .setName("User")
                    .setColor("#00FF00")
                    .setDiscordId(1L)
                    .setLuckpermsPermission("group.user")
                    .setAvailableForTwink(false)
                ),
            roles
        );
    }
}
