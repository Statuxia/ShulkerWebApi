package me.statuxia.shulkerapi.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.joda.time.DateTime;

import java.sql.Timestamp;

@Converter(autoApply = true)
public class JodaDateTimeConverter implements AttributeConverter<DateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(DateTime dateTime) {
        return (dateTime != null) ? new Timestamp(dateTime.getMillis()) : null;
    }

    @Override
    public DateTime convertToEntityAttribute(Timestamp timestamp) {
        return (timestamp != null) ? new DateTime(timestamp.getTime()) : null;
    }
}
