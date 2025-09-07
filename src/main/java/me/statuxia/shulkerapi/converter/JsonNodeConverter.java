package me.statuxia.shulkerapi.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import me.statuxia.shulkerapi.service.impl.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
@Component
public class JsonNodeConverter implements AttributeConverter<JsonNode, Map<String, String>> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final MessageService messageService;

    @Autowired
    public JsonNodeConverter(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public JsonNode convertToEntityAttribute(Map<String, String> dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return mapper.valueToTree(dbData);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, String> convertToDatabaseColumn(JsonNode attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return mapper.convertValue(attribute, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, String> convertToI18nMap(JsonNode attribute) {
        final Map<String, String> converted = convertToDatabaseColumn(attribute);
        if (converted == null) {
            return null;
        }

        final Map<String, String> result = new HashMap<>();
        converted.forEach((key, value) -> result.put(messageService.message(key), messageService.message(value)));
        return result;
    }
}
