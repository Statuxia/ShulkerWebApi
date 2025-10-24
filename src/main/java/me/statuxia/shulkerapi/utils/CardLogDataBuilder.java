package me.statuxia.shulkerapi.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.statuxia.shulkerapi.model.BankCardLogType;
import me.statuxia.shulkerapi.service.impl.MessageService;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CardLogDataBuilder {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Object> data = new HashMap<>();

    public CardLogDataBuilder action(BankCardLogType type) {
        return add("BankCardLog.data.action.title", MessageService.enumI18n(type));
    }

    public CardLogDataBuilder changes(String message) {
        return add("BankCardLog.data.valueChange.title", message);
    }

    public JsonNode getData() {
        return mapper.valueToTree(data);
    }

    public Map<String, Object> getMapData() {
        return data;
    }

    private CardLogDataBuilder add(String key, String value) {
        if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
            data.put(key, value);
        }
        return this;
    }
}
