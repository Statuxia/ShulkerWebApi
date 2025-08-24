package me.statuxia.shulkerapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class MessageService {

    private final MessageSource messageSource;
    private final Locale defaultLocale;

    @Autowired
    public MessageService(MessageSource messageSource, Locale defaultLocale) {
        this.messageSource = messageSource;
        this.defaultLocale = defaultLocale;
    }

    public String message(Object key) {
        return message(key, List.of());
    }

    public String message(Object key, List<Object> args) {
        return message(key, args, getDefaultLocale());
    }

    public String message(Object key, List<Object> args, Locale locale) {
        if (key instanceof Enum<?> enumKey) {
            return getMessageSource().getMessage(enumI18n(enumKey), args.toArray(), locale);
        }
        return getMessageSource().getMessage(String.valueOf(key), args.toArray(), locale);
    }

    private String enumI18n(Enum<?> arg) {
        if (arg == null) {
            return "";
        }

        return "enum.%s.%s".formatted(arg.getClass().getSimpleName(), arg.name());
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }
}
