package me.statuxia.shulkerapi.configuration;

import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final AuthDataResolver authDataResolver;

    @Autowired
    public WebConfiguration(AuthDataResolver authDataResolver) {
        this.authDataResolver = authDataResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authDataResolver);
    }
}
