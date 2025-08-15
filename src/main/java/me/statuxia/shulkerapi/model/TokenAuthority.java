package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.StringJoiner;

@Entity(name = "TokenAuthority")
@Table(name = "token_authority")
public class TokenAuthority implements Identifiable<String> {

    @Id
    @Column(name = "token", nullable = false)
    private String id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TokenAuthorityEnum authority;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TokenAuthority.class.getSimpleName() + "[", "]")
            .add("id='" + id + "'")
            .toString();
    }
}
