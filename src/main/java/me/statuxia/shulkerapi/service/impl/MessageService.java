package me.statuxia.shulkerapi.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class MessageService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

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
        String messageKey = String.valueOf(key);
        try {
            if (key instanceof Enum<?> enumKey) {
                messageKey = enumI18n(enumKey);
            }
            return getMessageSource().getMessage(messageKey, args.toArray(), locale);
        } catch (NoSuchMessageException ex) {
            logger.warn(ex.getMessage());
            return messageKey;
        }
    }

    public static String enumI18n(Enum<?> arg) {
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
