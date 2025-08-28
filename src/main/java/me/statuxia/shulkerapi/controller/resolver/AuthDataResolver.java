package me.statuxia.shulkerapi.controller.resolver;

import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.dao.CustomTokenDAO;
import me.statuxia.shulkerapi.dao.SessionTokenDAO;
import me.statuxia.shulkerapi.dao.TokenAuthorityDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.AuthenticationException;
import me.statuxia.shulkerapi.exception.AuthorityException;
import me.statuxia.shulkerapi.model.DisableAware;
import me.statuxia.shulkerapi.model.TokenAuthority;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import org.apache.commons.lang3.ArrayUtils;
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
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthDataResolver implements HandlerMethodArgumentResolver {

    public static final String X_TOKEN_HEADER = "X-Auth-Token";
    public static final List<Class<TokenData>> SUPPORTED_CLASSES = List.of(TokenData.class);

    private final CustomTokenDAO customTokenDAO;
    private final SessionTokenDAO sessionTokenDAO;
    private final TokenAuthorityDAO tokenAuthorityDAO;

    @Autowired
    public AuthDataResolver(
        CustomTokenDAO customTokenDAO, SessionTokenDAO sessionTokenDAO,
        TokenAuthorityDAO tokenAuthorityDAO
    ) {
        this.customTokenDAO = customTokenDAO;
        this.sessionTokenDAO = sessionTokenDAO;
        this.tokenAuthorityDAO = tokenAuthorityDAO;
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
            .orElseGet(() -> getBySessionToken(tokenValue)
            .orElseThrow());
        if (token.source() instanceof DisableAware disableAware && disableAware.isDisabled()) {
            throw AuthenticationException.TOKEN_DISABLED;
        }

        if (parameter.hasMethodAnnotation(RequiredAuthority.class)) {
            validateRequiredAuthority(parameter, token);
        }

        return token;
    }

    private void validateRequiredAuthority(MethodParameter parameter, TokenData token) {
        final RequiredAuthority requiredAuthority = parameter.getMethodAnnotation(RequiredAuthority.class);
        if (requiredAuthority == null) {
            return;
        }

        final TokenAuthorityEnum[] requireAll = requiredAuthority.requireAll();
        final TokenAuthorityEnum[] requireAny = requiredAuthority.requireAny();

        if (ArrayUtils.isEmpty(requireAll) && ArrayUtils.isEmpty(requireAny)) {
            return;
        }

        final Set<TokenAuthorityEnum> authorities = tokenAuthorityDAO.findByToken(token.token()).stream()
            .map(TokenAuthority::getAuthority)
            .collect(Collectors.toSet());

        if (ArrayUtils.isNotEmpty(requireAll)) {
            if (authorities.containsAll(List.of(requireAll))) {
                return;
            }

            throw AuthorityException.AUTHORITY_ACCESS_DENIED;
        }

        if (ArrayUtils.isNotEmpty(requireAll)) {
            for (TokenAuthorityEnum authority : requireAny) {
                if (authorities.contains(authority)) {
                    return;
                }
            }

            throw AuthorityException.AUTHORITY_ACCESS_DENIED;
        }
    }

    private Optional<TokenData> getByCustomToken(String token) {
        return getCustomTokenDAO().findByToken(token)
            .map(customToken -> new TokenData(token, customToken));
    }

    private Optional<TokenData> getBySessionToken(String token) {
        return getSessionTokenDAO().findByToken(token)
            .map(sessionToken -> new TokenData(token, sessionToken));
    }

    public CustomTokenDAO getCustomTokenDAO() {
        return customTokenDAO;
    }

    public SessionTokenDAO getSessionTokenDAO() {
        return sessionTokenDAO;
    }
}
