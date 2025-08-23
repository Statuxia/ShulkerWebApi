package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dao.SessionTokenDAO;
import me.statuxia.shulkerapi.dao.TokenAuthorityDAO;
import me.statuxia.shulkerapi.dao.TokenLimitationDAO;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.Account;
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

    @Mock
    protected SessionTokenDAO sessionTokenDAO;

    @Captor
    protected ArgumentCaptor<TokenLimitation> captor;

    @Test
    void noDataTest() {
        assertThrows(BaseApiException.class, () -> tokenService.createSessionToken(null));
    }

    @Test
    void createAndSaveTokenLimitation() {
        doReturn(new TokenLimitation()).when(tokenLimitationDAO).save(captor.capture());

        tokenService.createSessionToken(new Account());

        final TokenLimitation value = captor.getValue();
        assertEquals(SESSION_RATE_LIMIT, value.getRateLimit());
        assertEquals(SESSION_RATE_RESET_SECONDS, value.getRateResetSeconds());
    }
}