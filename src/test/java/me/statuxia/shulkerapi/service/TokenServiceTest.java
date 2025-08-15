package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dao.TokenAuthorityDAO;
import me.statuxia.shulkerapi.dao.TokenLimitationDAO;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.TokenLimitation;
import me.statuxia.shulkerapi.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static me.statuxia.shulkerapi.service.impl.TokenServiceImpl.SESSION_RATE_LIMIT;
import static me.statuxia.shulkerapi.service.impl.TokenServiceImpl.SESSION_RATE_RESET_SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    protected TokenServiceImpl tokenService;

    @Mock
    protected TokenLimitationDAO tokenLimitationDAO;

    @Mock
    protected TokenAuthorityDAO tokenAuthorityDAO;

    @Captor
    protected ArgumentCaptor<TokenLimitation> captor;

    @Test
    void noDataTest() {
        assertThrows(BaseApiException.class, () -> tokenService.createSessionToken(null));
    }

    @Test
    void createAndSaveTokenLimitation() {
        when(tokenLimitationDAO.findById(anyString())).thenReturn(Optional.empty());
        doReturn(new TokenLimitation()).when(tokenLimitationDAO).save(captor.capture());

        tokenService.createSessionToken(new DiscordAccount());

        final TokenLimitation value = captor.getValue();
        assertEquals(SESSION_RATE_LIMIT, value.getRateLimit());
        assertEquals(SESSION_RATE_RESET_SECONDS, value.getRateResetSeconds());
    }

    @Test
    void updateTokenLimitation() {
        final TokenLimitation tokenLimitation = new TokenLimitation();
        final String oldToken = "test";
        tokenLimitation.setId(oldToken);
        tokenLimitation.setRateLimit(66L);
        tokenLimitation.setRateResetSeconds(66L);
        when(tokenLimitationDAO.findById(anyString())).thenReturn(Optional.of(tokenLimitation));
        doReturn(new TokenLimitation()).when(tokenLimitationDAO).save(captor.capture());

        tokenService.createSessionToken(new DiscordAccount());

        final TokenLimitation captorValue = captor.getValue();
        assertEquals(tokenLimitation.getRateLimit(), captorValue.getRateLimit());
        assertEquals(tokenLimitation.getRateResetSeconds(), captorValue.getRateResetSeconds());
        assertNotEquals(oldToken, captorValue.getId());
    }
}