package me.statuxia.shulkerapi.controller.resolver;

import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.dao.CustomTokenDAO;
import me.statuxia.shulkerapi.dao.DiscordAccountDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.AuthenticationException;
import me.statuxia.shulkerapi.model.DisableAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;
import java.util.Optional;

@Component
public class AuthDataResolver implements HandlerMethodArgumentResolver {

    public static final String X_TOKEN_HEADER = "X-Auth-Token";
    public static final List<Class<TokenData>> SUPPORTED_CLASSES = List.of(TokenData.class);

    private final DiscordAccountDAO discordAccountDAO;
    private final CustomTokenDAO customTokenDAO;

    @Autowired
    public AuthDataResolver(DiscordAccountDAO discordAccountDAO, CustomTokenDAO customTokenDAO) {
        this.discordAccountDAO = discordAccountDAO;
        this.customTokenDAO = customTokenDAO;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(AuthData.class)) {
            return SUPPORTED_CLASSES.contains(parameter.getParameterType());
        }

        return false;
    }

    @Nullable
    @Override
    public Object resolveArgument(
        MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory
    ) {
        final String tokenValue = webRequest.getHeader(X_TOKEN_HEADER);

        if (!StringUtils.hasText(tokenValue)) {
            throw AuthenticationException.UNKNOWN_TOKEN;
        }

        final TokenData token = getByCustomToken(tokenValue)
            .orElseGet(() -> getByDiscordAccount(tokenValue)
            .orElseThrow());
        if (token.source() instanceof DisableAware disableAware && disableAware.isDisabled()) {
            throw AuthenticationException.TOKEN_DISABLED;
        }

        return token;
    }

    private Optional<TokenData> getByCustomToken(String token) {
        return getCustomTokenDAO().findByToken(token)
            .map(customToken -> new TokenData(token, customToken));
    }

    private Optional<TokenData> getByDiscordAccount(String token) {
        return getDiscordAccountDAO().findBySessionToken(token)
            .map(discordAccount -> new TokenData(token, discordAccount));
    }

    public DiscordAccountDAO getDiscordAccountDAO() {
        return discordAccountDAO;
    }

    public CustomTokenDAO getCustomTokenDAO() {
        return customTokenDAO;
    }
}
