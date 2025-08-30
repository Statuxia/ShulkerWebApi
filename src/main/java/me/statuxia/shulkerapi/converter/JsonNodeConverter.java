package me.statuxia.shulkerapi.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Converter(autoApply = true)
@Component
public class JsonNodeConverter implements AttributeConverter<JsonNode, Map<String, String>> {

    private final ObjectMapper mapper = new ObjectMapper();

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
}
