package me.statuxia.shulkerapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@Configuration
public class RootConfiguration {

    @Bean("defaultLocale")
    public Locale defaultLocale() {
        return Locale.of("ru", "RU");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
