package me.statuxia.shulkerapi.model;

import java.io.Serializable;
import java.util.Objects;

public class TokenAuthorityId implements Serializable {
    private String token;
    private TokenAuthorityEnum authority;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TokenAuthorityId that = (TokenAuthorityId) o;
        return Objects.equals(token, that.token) && authority == that.authority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, authority);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TokenAuthorityEnum getAuthority() {
        return authority;
    }

    public void setAuthority(TokenAuthorityEnum authority) {
        this.authority = authority;
    }
}
