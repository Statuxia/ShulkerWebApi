package me.statuxia.shulkerapi.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CardHistoryDataBuilder {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Object> data = new HashMap<>();

    public CardHistoryDataBuilder description(String description) {
        return add("description", description);
    }

    public CardHistoryDataBuilder markAsAdmin() {
        return add("markAsAdmin", "true");
    }

    public CardHistoryDataBuilder valueChange(String message) {
        return add("valueChange", message);
    }

    public CardHistoryDataBuilder fromTo(Long from, Long to) {
        return add("oldBalance", String.valueOf(from == null ? 0 : from))
            .add("newBalance", String.valueOf(to == null ? 0 : to));
    }

    public CardHistoryDataBuilder dataChange(String message) {
        return add("dataChange", message);
    }

    public JsonNode getData() {
        return mapper.valueToTree(data);
    }

    public Map<String, Object> getMapData() {
        return data;
    }

    private CardHistoryDataBuilder add(String key, String value) {
        if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
            data.put(key, value);
        }
        return this;
    }
}
