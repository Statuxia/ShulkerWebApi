package me.statuxia.shulkerapi.configuration;

import me.statuxia.shulkerapi.controller.filter.TokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    private final TokenFilter tokenFilter;

    @Autowired
    public FilterConfiguration(TokenFilter tokenFilter) {
        this.tokenFilter = tokenFilter;
    }

    @Bean
    public FilterRegistrationBean<TokenFilter> rateLimitFilter() {
        final FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(tokenFilter);
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }

}
