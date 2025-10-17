package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.configuration.properties.SessionLimitationProperties;
import me.statuxia.shulkerapi.dao.SessionTokenDAO;
import me.statuxia.shulkerapi.dao.TokenAuthorityDAO;
import me.statuxia.shulkerapi.dao.TokenLimitationDAO;
import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.SessionToken;
import me.statuxia.shulkerapi.model.TokenAuthority;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.model.TokenLimitation;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.service.ValidationService;
import me.statuxia.shulkerapi.utils.TokenGenerator;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {

    private final SessionLimitationProperties sessionLimitationProperties;
    private final TokenLimitationDAO tokenLimitationDAO;
    private final TokenAuthorityDAO tokenAuthorityDAO;
    private final SessionTokenDAO sessionTokenDAO;
    private final ValidationService validationService;

    public TokenServiceImpl(
        SessionLimitationProperties sessionLimitationProperties,
        TokenLimitationDAO tokenLimitationDAO,
        TokenAuthorityDAO tokenAuthorityDAO,
        SessionTokenDAO sessionTokenDAO,
        ValidationService validationService
    ) {
        this.sessionLimitationProperties = sessionLimitationProperties;
        this.tokenLimitationDAO = tokenLimitationDAO;
        this.tokenAuthorityDAO = tokenAuthorityDAO;
        this.sessionTokenDAO = sessionTokenDAO;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public String createSessionToken(Account account) {
        getValidationService().validateAccount(account);

        final String newToken = TokenGenerator.generate();

        createSessionToken(account, newToken);
        createLimitation(newToken);
        createAuthorities(newToken);

        return newToken;
    }

    private void createSessionToken(Account account, String token) {
        final SessionToken sessionToken = new SessionToken();
        sessionToken.setToken(token);
        sessionToken.setAccount(account);
        sessionToken.setCreateTime(DateTime.now());

        getSessionTokenDAO().save(sessionToken);
    }

    @Override
    @Transactional
    public boolean hasAuthority(String token, TokenAuthorityEnum authority) {
        return tokenAuthorityDAO.existsByTokenAndAuthority(token, authority);
    }

    private void createLimitation(String token) {
        final TokenLimitation limitation = new TokenLimitation();
        limitation.setId(token);
        limitation.setRateLimit(sessionLimitationProperties.getSessionRateLimit());
        limitation.setRateResetSeconds(sessionLimitationProperties.getSessionRateResetSeconds());

        getTokenLimitationDAO().save(limitation);
    }

    private void createAuthorities(String token) {
        final List<TokenAuthority> tokenAuthorities = getDefaultAuthorities(token);
        tokenAuthorities.forEach(getTokenAuthorityDAO()::save);
    }

    private List<TokenAuthority> getDefaultAuthorities(String token) {
        final ArrayList<TokenAuthority> authorities = new ArrayList<>();
        return authorities;
    }

    public TokenLimitationDAO getTokenLimitationDAO() {
        return tokenLimitationDAO;
    }

    public TokenAuthorityDAO getTokenAuthorityDAO() {
        return tokenAuthorityDAO;
    }

    public SessionTokenDAO getSessionTokenDAO() {
        return sessionTokenDAO;
    }

    public ValidationService getValidationService() {
        return validationService;
    }
}
