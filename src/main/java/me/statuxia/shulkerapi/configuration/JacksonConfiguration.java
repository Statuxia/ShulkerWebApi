package me.statuxia.shulkerapi.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.statuxia.shulkerapi.converter.JodaDateTimeDeserializer;
import me.statuxia.shulkerapi.converter.JodaDateTimeSerializer;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(DateTime.class, new JodaDateTimeDeserializer());
        module.addSerializer(DateTime.class, new JodaDateTimeSerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
