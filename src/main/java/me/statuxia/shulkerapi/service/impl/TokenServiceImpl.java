package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.TokenAuthorityDAO;
import me.statuxia.shulkerapi.dao.TokenLimitationDAO;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.TokenAuthority;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.model.TokenLimitation;
import me.statuxia.shulkerapi.service.TokenService;
import me.statuxia.shulkerapi.utils.TokenGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {

    public static final long SESSION_RATE_LIMIT = 120L;
    public static final long SESSION_RATE_RESET_SECONDS = 60L;

    private final TokenLimitationDAO tokenLimitationDAO;
    private final TokenAuthorityDAO tokenAuthorityDAO;

    public TokenServiceImpl(TokenLimitationDAO tokenLimitationDAO, TokenAuthorityDAO tokenAuthorityDAO) {
        this.tokenLimitationDAO = tokenLimitationDAO;
        this.tokenAuthorityDAO = tokenAuthorityDAO;
    }

    @Override
    @Transactional
    public String createSessionToken(DiscordAccount discordAccount) {
        if (discordAccount == null) {
            throw BaseApiException.INCORRECT_DATA;
        }

        final String newToken = TokenGenerator.generate();
        String oldToken = discordAccount.getSessionToken();
        if (!StringUtils.hasText(oldToken)) {
            oldToken = "-";
        }


        processLimitation(oldToken, newToken);
        processAuthorities(oldToken, newToken);

        return newToken;
    }

    @Override
    @Transactional
    public boolean hasAuthority(String token, TokenAuthorityEnum authority) {
        return tokenAuthorityDAO.hasAuthorityByIdAndAuthority(token, authority);
    }

    private void processLimitation(String oldToken, String newToken) {
        final TokenLimitation limitation = getLimitation(oldToken);
        if (StringUtils.hasText(limitation.getId())) {
            getTokenLimitationDAO().delete(limitation);
        }
        final TokenLimitation copy = limitation.copy();
        copy.setId(newToken);
        getTokenLimitationDAO().save(copy);
    }

    private void processAuthorities(String oldToken, String newToken) {
        final List<TokenAuthority> tokenAuthorities = getTokenAuthorities(oldToken);

        getTokenAuthorityDAO().saveAll(tokenAuthorities.stream().map(k -> {
            final TokenAuthority copy = k.copy();
            copy.setId(newToken);
            return copy;
        }).toList());
    }

    private TokenLimitation getLimitation(String oldToken) {
        return getTokenLimitationDAO().findById(oldToken).orElseGet(() -> {
            final TokenLimitation newLimitation = new TokenLimitation();
            newLimitation.setRateLimit(SESSION_RATE_LIMIT);
            newLimitation.setRateResetSeconds(SESSION_RATE_RESET_SECONDS);
            return newLimitation;
        });
    }

    private List<TokenAuthority> getTokenAuthorities(String oldToken) {
        final List<TokenAuthority> authorities = getTokenAuthorityDAO().findByToken(oldToken);
        getTokenAuthorityDAO().deleteAll(authorities);
        return authorities;
    }

    public TokenLimitationDAO getTokenLimitationDAO() {
        return tokenLimitationDAO;
    }

    public TokenAuthorityDAO getTokenAuthorityDAO() {
        return tokenAuthorityDAO;
    }
}
