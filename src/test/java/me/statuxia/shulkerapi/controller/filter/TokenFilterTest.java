package me.statuxia.shulkerapi.controller.filter;

import jakarta.servlet.ServletException;
import me.statuxia.shulkerapi.controller.advice.ControllerExceptionHandler;
import me.statuxia.shulkerapi.dao.TokenLimitationDAO;
import me.statuxia.shulkerapi.dto.RateLimit;
import me.statuxia.shulkerapi.model.TokenLimitation;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.statuxia.shulkerapi.controller.filter.TokenFilter.*;
import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenFilterTest {

    public static final List<@NotNull String> EXPECTED_HEADERS = List.of(X_RATE_LIMIT_LIMIT, X_RATE_LIMIT_RESET, X_RATE_LIMIT_REMAINING);
    @InjectMocks
    protected TokenFilter filter;

    @Mock
    protected TokenLimitationDAO tokenLimitationDAO;

    @Mock
    protected ControllerExceptionHandler controllerExceptionHandler;

    @ParameterizedTest
    @MethodSource("successProcessDataSource")
    void successProcessTest(TokenLimitation limitation) throws ServletException, IOException {
        final String token = "test";
        when(tokenLimitationDAO.findById(anyString())).thenReturn(Optional.of(limitation));

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(X_TOKEN_HEADER, token);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        if (limitation.getRateLimit() <= 0 || limitation.getRateResetSeconds() <= 0) {
            assertFalse(filter.getRateLimits().containsKey(token));
            assertFalse(response.getHeaderNames().containsAll(EXPECTED_HEADERS));
            return;
        }

        assertTrue(filter.getRateLimits().containsKey(token));
        final RateLimit rateLimit = filter.getRateLimits().get(token);
        assertEquals(limitation.getRateLimit(), rateLimit.getLimit());
        assertEquals(limitation.getRateLimit() - 1, rateLimit.getRemaining());

        assertTrue(response.getHeaderNames().containsAll(EXPECTED_HEADERS));
        assertEquals(String.valueOf(rateLimit.getRemaining()), response.getHeader(X_RATE_LIMIT_REMAINING));
    }

    @Test
    void whitelistUrlTest() throws ServletException, IOException {
        final String token = "test";

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/oauth2/discord");
        request.addHeader(X_TOKEN_HEADER, token);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertFalse(filter.getRateLimits().containsKey(token));
        assertFalse(response.getHeaderNames().containsAll(EXPECTED_HEADERS));
    }

    @Test
    void limitReachedTest() throws ServletException, IOException {
        final String token = "test";
        final TokenLimitation limitation = buildTokenLimitation(1000, 1);
        when(tokenLimitationDAO.findById(anyString())).thenReturn(Optional.of(limitation));

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(X_TOKEN_HEADER, token);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);
        filter.doFilter(request, response, filterChain);
        assertTrue(response.getHeaderNames().containsAll(EXPECTED_HEADERS));
        assertEquals(401, response.getStatus());
    }

    public static Stream<Arguments> successProcessDataSource() {
        return Stream.of(
            Arguments.of(buildTokenLimitation(1000L, 60)),
            Arguments.of(buildTokenLimitation(1000L, 0)),
            Arguments.of(buildTokenLimitation(0, 60))
        );
    }

    private static TokenLimitation buildTokenLimitation(long rateResetSeconds, long rateLimit) {
        final TokenLimitation limitation = new TokenLimitation();
        limitation.setRateResetSeconds(rateResetSeconds);
        limitation.setRateLimit(rateLimit);
        return limitation;
    }
}
