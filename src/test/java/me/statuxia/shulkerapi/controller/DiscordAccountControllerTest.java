package me.statuxia.shulkerapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;
import me.statuxia.shulkerapi.service.DiscordIntegrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
    "classpath:sql/DiscordAccountControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@Transactional
class DiscordAccountControllerTest extends BaseContainerTest {

    private static final String SESSION_TOKEN = "session-token-1";

    @MockitoBean
    protected DiscordIntegrationService discordIntegrationService;

    @Autowired
    protected MockMvc mockMvc;

    @Test
    void getSuccessById() throws Exception {
        final DiscordIdentityResponse expectedResponse = new DiscordIdentityResponse();
        expectedResponse.setId(1234567890L);
        expectedResponse.setUsername("testUsername");
        expectedResponse.setAvatar("qw724vyt12n984v");
        final HttpHandler.HttpResponse<DiscordIdentityResponse> httpResponse = new HttpHandler.HttpResponse<>(
            expectedResponse, HttpStatus.OK
        );
        when(discordIntegrationService.getUserInfo(anyString())).thenReturn(httpResponse);

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordAccountController.PREFIX + DiscordAccountController.GET + "/1")
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(new ObjectMapper().writeValueAsString(expectedResponse), result.getResponse().getContentAsString());
    }

    @Test
    void getSuccess() throws Exception {
        final DiscordIdentityResponse expectedResponse = new DiscordIdentityResponse();
        expectedResponse.setId(1234567890L);
        expectedResponse.setUsername("testUsername");
        expectedResponse.setAvatar("qw724vyt12n984v");
        final HttpHandler.HttpResponse<DiscordIdentityResponse> httpResponse = new HttpHandler.HttpResponse<>(
            expectedResponse, HttpStatus.OK
        );
        when(discordIntegrationService.getUserInfo(anyString())).thenReturn(httpResponse);

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordAccountController.PREFIX + DiscordAccountController.GET)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andReturn();

        assertEquals(new ObjectMapper().writeValueAsString(expectedResponse), result.getResponse().getContentAsString());
    }

    @Test
    void getNoToken() throws Exception {
        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordAccountController.PREFIX + DiscordAccountController.GET)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized())
            .andReturn();

        final JsonNode node = new ObjectMapper().readValue(result.getResponse().getContentAsString(), JsonNode.class);
        assertEquals(1101, node.get("code").asInt());
    }

    @Test
    void getInvalidToken() throws Exception {
        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordAccountController.PREFIX + DiscordAccountController.GET)
                    .header(X_TOKEN_HEADER, "test")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized())
            .andReturn();

        final JsonNode node = new ObjectMapper().readValue(result.getResponse().getContentAsString(), JsonNode.class);
        assertEquals(1105, node.get("code").asInt());
    }

    @Test
    void getExpiredToken() throws Exception {
        when(discordIntegrationService.getUserInfo(anyString())).thenReturn(new HttpHandler.HttpResponse<>(new Exception("any")));

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordAccountController.PREFIX + DiscordAccountController.GET)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized())
            .andReturn();

        final JsonNode node = new ObjectMapper().readValue(result.getResponse().getContentAsString(), JsonNode.class);
        assertEquals(1104, node.get("code").asInt());
    }

    @Test
    void getNoData() throws Exception {
        when(discordIntegrationService.getUserInfo(anyString())).thenReturn(new HttpHandler.HttpResponse<>(null, null));

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordAccountController.PREFIX + DiscordAccountController.GET)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest())
            .andReturn();

        final JsonNode node = new ObjectMapper().readValue(result.getResponse().getContentAsString(), JsonNode.class);
        assertEquals(1001, node.get("code").asInt());
    }

    @Test
    void getEmptyData() throws Exception {
        when(discordIntegrationService.getUserInfo(anyString())).thenReturn(new HttpHandler.HttpResponse<>(null, null));

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordAccountController.PREFIX + DiscordAccountController.GET)
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest())
            .andReturn();

        final JsonNode node = new ObjectMapper().readValue(result.getResponse().getContentAsString(), JsonNode.class);
        assertEquals(1001, node.get("code").asInt());
    }

    @Test
    void getUnknownAccount() throws Exception {
        when(discordIntegrationService.getUserInfo(anyString())).thenReturn(new HttpHandler.HttpResponse<>(null, null));

        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordAccountController.PREFIX + DiscordAccountController.GET + "/9999")
                    .header(X_TOKEN_HEADER, SESSION_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest())
            .andReturn();

        final JsonNode node = new ObjectMapper().readValue(result.getResponse().getContentAsString(), JsonNode.class);
        assertEquals(1201, node.get("code").asInt());
    }
}
