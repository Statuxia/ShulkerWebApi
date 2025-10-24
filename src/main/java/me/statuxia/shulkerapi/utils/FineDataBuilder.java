package me.statuxia.shulkerapi.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class FineDataBuilder {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Object> data = new HashMap<>();

    public FineDataBuilder oldNewValue(Object oldValue, Object newValue) {
        return add("oldValue", String.valueOf(oldValue == null ? "" : oldValue))
            .add("newValue", String.valueOf(newValue == null ? "" : newValue));
    }

    public JsonNode getData() {
        return mapper.valueToTree(data);
    }

    public Map<String, Object> getMapData() {
        return data;
    }

    private FineDataBuilder add(String key, String value) {
        if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
            data.put(key, value);
        }
        return this;
    }
}
