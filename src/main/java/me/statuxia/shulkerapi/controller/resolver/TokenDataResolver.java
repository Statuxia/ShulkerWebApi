package me.statuxia.shulkerapi.controller.resolver;

import me.statuxia.shulkerapi.annotations.TokenData;
import me.statuxia.shulkerapi.exception.AuthenticationException;
import me.statuxia.shulkerapi.model.Token;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

@Component
public class TokenDataResolver implements HandlerMethodArgumentResolver {

    /**
     * Сессионный токен пользователя
     */
    public static final String X_SESSION_TOKEN_HEADER = "X-Session-Token";

    /**
     * Токен стороннего разработчика (создается в ЛК)
     */
    public static final String X_DEV_TOKEN_HEADER = "X-Dev-Token";

    public static final List<Class<Token>> SUPPORTED_CLASSES = List.of(Token.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(TokenData.class)) {
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
        final String sessionToken = webRequest.getHeader(X_SESSION_TOKEN_HEADER);

        if (!StringUtils.hasText(sessionToken)) {
            throw AuthenticationException.UNKNOWN_SESSION_TOKEN;
        }

        return new Token(sessionToken);
    }
}
