package me.statuxia.shulkerapi.controller.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import me.statuxia.shulkerapi.controller.filter.request.CachedBodyHttpServletRequest;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;

@Component
public class LogFilter implements Filter {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        final HttpServletRequest httpRequest = new CachedBodyHttpServletRequest((HttpServletRequest) request);

        logger.info(
            """
                Request Body: {}
                X-Auth-Token: {}
                IP Address: {}
                Request URL: {} | Method: {}
                """,
            getRequestBody(httpRequest),
            httpRequest.getHeader(AuthDataResolver.X_TOKEN_HEADER),
            httpRequest.getRemoteAddr(),
            httpRequest.getRequestURL().toString(),
            httpRequest.getMethod()
        );

        chain.doFilter(httpRequest, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        final BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}
