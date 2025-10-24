package me.statuxia.shulkerapi.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.statuxia.shulkerapi.utils.DateUtils;
import org.joda.time.DateTime;

import java.io.IOException;

public class JodaDateTimeSerializer extends JsonSerializer<DateTime> {

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeString(value.toString(DateUtils.DATETIME));
        }
    }
}
