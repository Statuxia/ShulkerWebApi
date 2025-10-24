package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.StringJoiner;

@Entity(name = "TokenAuthority")
@Table(name = "token_authority")
@IdClass(TokenAuthorityId.class)
public class TokenAuthority implements Identifiable<String> {

    @Id
    @Column(name = "token", nullable = false)
    private String token;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TokenAuthorityEnum authority;

    public TokenAuthority() {
    }

    public TokenAuthority(TokenAuthority authority) {
        this.token = authority.getId();
        this.authority = authority.authority;
    }

    @Override
    public String getId() {
        return token;
    }

    @Override
    public void setId(String id) {
        this.token = id;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TokenAuthority that = (TokenAuthority) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(token);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TokenAuthority.class.getSimpleName() + "[", "]")
            .add("id='" + token + "'")
            .toString();
    }

    public TokenAuthority copy() {
        return new TokenAuthority(this);
    }
}
