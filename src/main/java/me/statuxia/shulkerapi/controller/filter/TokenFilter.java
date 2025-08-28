package me.statuxia.shulkerapi.controller.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.statuxia.shulkerapi.controller.advice.ControllerExceptionHandler;
import me.statuxia.shulkerapi.dao.TokenLimitationDAO;
import me.statuxia.shulkerapi.dto.RateLimit;
import me.statuxia.shulkerapi.exception.ApiException;
import me.statuxia.shulkerapi.exception.AuthenticationException;
import me.statuxia.shulkerapi.model.TokenLimitation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static me.statuxia.shulkerapi.controller.resolver.AuthDataResolver.X_TOKEN_HEADER;

@Component
public class TokenFilter implements Filter {

    public static final String X_RATE_LIMIT_LIMIT = "X-RateLimit-Limit";
    public static final String X_RATE_LIMIT_RESET = "X-RateLimit-Reset";
    public static final String X_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
    private final Map<String, RateLimit> rateLimits = new ConcurrentHashMap<>();
    private final List<String> whitelistUrls = List.of(
        "/api/v1/oauth2/discord"
    );

    private final TokenLimitationDAO tokenLimitationDAO;
    private final ControllerExceptionHandler handler;

    @Autowired
    public TokenFilter(TokenLimitationDAO tokenLimitationDAO, ControllerExceptionHandler handler) {
        this.tokenLimitationDAO = tokenLimitationDAO;
        this.handler = handler;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        try {
            process(request, response, chain);
        } catch (ApiException e) {
            if (response instanceof HttpServletResponse httpServletResponse) {
                final Object json = handler.handle(e, httpServletResponse);
                httpServletResponse.setStatus(e.getStatus().value());
                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                httpServletResponse.setCharacterEncoding("UTF-8");
                try (PrintWriter writer = httpServletResponse.getWriter()) {
                    writer.write(new ObjectMapper().writeValueAsString(json));
                    writer.flush();
                }
            }
        }
    }

    private void process(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest httpServletRequest
            && response instanceof HttpServletResponse httpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        for (String url : whitelistUrls) {
            if (httpServletRequest.getRequestURI().startsWith(url)) {
                chain.doFilter(request, response);
                return;
            }
        }

        final String token = httpServletRequest.getHeader(X_TOKEN_HEADER);

        final TokenLimitation limitation = validateToken(token);

        if (limitation.getRateLimit() <= 0 || limitation.getRateResetSeconds() <= 0) {
            chain.doFilter(request, response);
            return;
        }

        final RateLimit rateLimit = getRateLimit(token, limitation);

        if (rateLimit.getRemaining() == 0) {
            buildHeaders(httpServletResponse, rateLimit);
            throw AuthenticationException.TOKEN_LIMIT_REACHED;
        }

        rateLimit.decrement();

        buildHeaders(httpServletResponse, rateLimit);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private RateLimit getRateLimit(String token, TokenLimitation limitation) {
        final RateLimit rateLimit = rateLimits.computeIfAbsent(token, x -> new RateLimit(limitation));
        if (rateLimit.getReset() < System.nanoTime()) {
            rateLimit.update(limitation);
        }
        return rateLimit;
    }

    public Map<String, RateLimit> getRateLimits() {
        return this.rateLimits.entrySet().stream()
            .peek(entry -> entry.setValue(new RateLimit(entry.getValue())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private TokenLimitation validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw AuthenticationException.UNKNOWN_TOKEN;
        }

        return getTokenLimitationDAO().findById(token)
            .orElseThrow(() -> AuthenticationException.TOKEN_HAS_NO_LIMITATION);
    }

    private void buildHeaders(HttpServletResponse httpServletResponse, RateLimit rateLimit) {
        httpServletResponse.setHeader(X_RATE_LIMIT_LIMIT, String.valueOf(rateLimit.getLimit()));
        httpServletResponse.setHeader(X_RATE_LIMIT_RESET, String.valueOf(rateLimit.getReset()));
        httpServletResponse.setHeader(X_RATE_LIMIT_REMAINING, String.valueOf(rateLimit.getRemaining()));
    }

    public TokenLimitationDAO getTokenLimitationDAO() {
        return tokenLimitationDAO;
    }
}
