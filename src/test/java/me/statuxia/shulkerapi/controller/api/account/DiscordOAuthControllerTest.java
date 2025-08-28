package me.statuxia.shulkerapi.controller.api.account;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.handler.HttpHandler;
import me.statuxia.shulkerapi.response.DiscordAccessTokenResponse;
import me.statuxia.shulkerapi.response.DiscordIdentityResponse;
import me.statuxia.shulkerapi.service.DiscordIntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
    "classpath:sql/DiscordAccountControllerTest.sql"
})
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@Transactional
@TestPropertySource("classpath:test-application.properties")
@DirtiesContext
class DiscordOAuthControllerTest extends BaseContainerTest {

    @MockitoBean
    protected DiscordIntegrationService discordIntegrationService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected DiscordAccountDAO discordAccountDAO;

    @Value("${discord.oauth.clientId}")
    protected String clientId;

    @Value("${discord.oauth.redirectUri}")
    protected String redirectUri;

    protected String expectedRedirectUri;

    @BeforeEach
    void setUp() {
        expectedRedirectUri = "https://discord.com/api/oauth2/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=identify"
            .formatted(clientId, redirectUri);
    }

    @Test
    void redirectSuccess() throws Exception {
        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(DiscordOAuthController.PREFIX + DiscordOAuthController.REDIRECT)
        ).andReturn();

        assertEquals(expectedRedirectUri, result.getResponse().getRedirectedUrl());
    }

    @Test
    void authNoCode() throws Exception {
        final MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(DiscordOAuthController.PREFIX + DiscordOAuthController.AUTH)
        ).andExpect(status().isBadRequest()).andReturn();


        final JsonNode node = new ObjectMapper().readValue(result.getResponse().getContentAsString(), JsonNode.class);
        assertEquals(1000, node.get("code").asInt());
    }

    @Test
    void authWrongCode() throws Exception {
        when(discordIntegrationService.getAccessToken(anyString())).thenReturn(new HttpHandler.HttpResponse<>(new Exception("any")));

        mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordOAuthController.PREFIX + DiscordOAuthController.AUTH + "?code=")
            ).andExpect(status().isFound())
            .andExpect(redirectedUrl("http://localhost:8080/failure"));
    }

    @Test
    void authNoCodeData() throws Exception {
        when(discordIntegrationService.getAccessToken(anyString())).thenReturn(new HttpHandler.HttpResponse<>(null, null));

        mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordOAuthController.PREFIX + DiscordOAuthController.AUTH + "?code=")
            ).andExpect(status().isFound())
            .andExpect(redirectedUrl("http://localhost:8080/failure"));
    }

    @Test
    void authWrongAccessToken() throws Exception {
        when(discordIntegrationService.getAccessToken(anyString())).thenReturn(new HttpHandler.HttpResponse<>(new DiscordAccessTokenResponse().setAccessToken("bad-token"), null));
        when(discordIntegrationService.getUserInfo(anyString())).thenReturn(new HttpHandler.HttpResponse<>(new Exception("any")));

        mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordOAuthController.PREFIX + DiscordOAuthController.AUTH + "?code=")
            ).andExpect(status().isFound())
            .andExpect(redirectedUrl("http://localhost:8080/failure"));
    }

    @Test
    void authNoUserData() throws Exception {
        when(discordIntegrationService.getAccessToken(anyString())).thenReturn(new HttpHandler.HttpResponse<>(new DiscordAccessTokenResponse().setAccessToken("bad-token"), null));
        when(discordIntegrationService.getUserInfo(anyString())).thenReturn(new HttpHandler.HttpResponse<>(null, null));

        mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordOAuthController.PREFIX + DiscordOAuthController.AUTH + "?code=")
            ).andExpect(status().isFound())
            .andExpect(redirectedUrl("http://localhost:8080/failure"));
    }

    @Test
    void authUpdateDiscordAccount() throws Exception {
        final DiscordAccessTokenResponse accessTokenResponse = new DiscordAccessTokenResponse()
            .setExpireIn(1000000L)
            .setRefreshToken("refresh-token-1-upd")
            .setAccessToken("access-token-1-upd");
        when(discordIntegrationService.getAccessToken(anyString())).thenReturn(new HttpHandler.HttpResponse<>(accessTokenResponse, null));
        final DiscordIdentityResponse identityResponse = new DiscordIdentityResponse();
        identityResponse.setId(1L);
        identityResponse.setUsername("testUsername");
        identityResponse.setAvatar("qw724vyt12n984v");
        when(discordIntegrationService.getUserInfo(anyString())).thenReturn(new HttpHandler.HttpResponse<>(identityResponse, null));

        assertEquals(1, discordAccountDAO.count());

        mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordOAuthController.PREFIX + DiscordOAuthController.AUTH + "?code=")
            ).andExpect(status().isFound())
            .andExpect(mvc -> assertTrue(mvc.getResponse().getRedirectedUrl().startsWith("http://localhost:8080/success")))
            .andExpect(mvc -> assertEquals(1, discordAccountDAO.count()));
    }

    @Test
    void authInsertDiscordAccount() throws Exception {
        final DiscordAccessTokenResponse accessTokenResponse = new DiscordAccessTokenResponse()
            .setExpireIn(1000000L)
            .setRefreshToken("refresh-token-1-upd")
            .setAccessToken("access-token-1-upd");
        when(discordIntegrationService.getAccessToken(anyString())).thenReturn(new HttpHandler.HttpResponse<>(accessTokenResponse, null));
        final DiscordIdentityResponse identityResponse = new DiscordIdentityResponse();
        identityResponse.setId(2L);
        identityResponse.setUsername("testUsername");
        identityResponse.setAvatar("qw724vyt12n984v");
        when(discordIntegrationService.getUserInfo(anyString())).thenReturn(new HttpHandler.HttpResponse<>(identityResponse, null));

        assertEquals(1, discordAccountDAO.count());

        mockMvc.perform(
                MockMvcRequestBuilders.get(DiscordOAuthController.PREFIX + DiscordOAuthController.AUTH + "?code=")
            ).andExpect(status().isFound())
            .andExpect(mvc -> assertTrue(mvc.getResponse().getRedirectedUrl().startsWith("http://localhost:8080/success")))
            .andExpect(mvc -> assertEquals(2, discordAccountDAO.count()));
    }
}