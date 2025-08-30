package me.statuxia.shulkerapi.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class DateUtils {

    public static final DateTimeFormatter DATETIME_ISO = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter LOCAL_DATE_ISO = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss");
    public static final DateTimeFormatter LOCAL_DATE = DateTimeFormat.forPattern("dd.MM.yyyy");

    private DateUtils() {
    }
}
