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

    public String message(String key) {
        return message(key, List.of());
    }

    public String message(String key, List<Object> args) {
        return message(key, args, getDefaultLocale());
    }

    public String message(String key, List<Object> args, Locale locale) {
        return getMessageSource().getMessage(key, args.toArray(), locale);
    }


    public MessageSource getMessageSource() {
        return messageSource;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }
}
