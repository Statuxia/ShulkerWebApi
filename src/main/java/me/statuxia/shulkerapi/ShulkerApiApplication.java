package me.statuxia.shulkerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource(value = "file:/etc/shulkerapiweb/application.properties")
})
public class ShulkerApiApplication {

    private ShulkerApiApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(ShulkerApiApplication.class, args);
    }
}
