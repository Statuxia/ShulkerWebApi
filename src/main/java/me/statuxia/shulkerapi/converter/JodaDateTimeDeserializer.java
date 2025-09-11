package me.statuxia.shulkerapi.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import me.statuxia.shulkerapi.utils.DateUtils;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class JodaDateTimeDeserializer extends JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
        final String date = p.getText();
        if (!StringUtils.hasText(date)) {
            return null;
        }
        return DateTime.parse(date, DateUtils.DATETIME);
    }
}
