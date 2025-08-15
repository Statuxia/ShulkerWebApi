package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.TokenAuthorityDAO;
import me.statuxia.shulkerapi.dao.TokenLimitationDAO;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.DiscordAccount;
import me.statuxia.shulkerapi.model.TokenAuthority;
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

        getTokenLimitationDAO().save(getLimitation(oldToken, newToken));
        getTokenAuthorityDAO().saveAll(getTokenAuthorities(oldToken, newToken));

        return newToken;
    }

    private TokenLimitation getLimitation(String oldToken, String newToken) {
        final TokenLimitation limitation = getTokenLimitationDAO().findById(oldToken).orElseGet(() -> {
            final TokenLimitation newLimitation = new TokenLimitation();
            newLimitation.setRateLimit(SESSION_RATE_LIMIT);
            newLimitation.setRateResetSeconds(SESSION_RATE_RESET_SECONDS);
            return newLimitation;
        });
        limitation.setId(newToken);
        return limitation;
    }

    private List<TokenAuthority> getTokenAuthorities(String oldToken, String newToken) {
        final List<TokenAuthority> authorities = getTokenAuthorityDAO().findAllById(List.of(oldToken));
        authorities.forEach(authority -> authority.setId(newToken));
        return authorities;
    }

    public TokenLimitationDAO getTokenLimitationDAO() {
        return tokenLimitationDAO;
    }

    public TokenAuthorityDAO getTokenAuthorityDAO() {
        return tokenAuthorityDAO;
    }
}
