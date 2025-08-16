package me.statuxia.shulkerapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.StringJoiner;

@Entity(name = "TokenLimitation")
@Table(name = "token_limitation")
public class TokenLimitation implements Identifiable<String> {

    @Id
    @Column(name = "token", nullable = false)
    private String id;

    @Column(name = "rate_limit", nullable = false)
    private Long rateLimit;

    @Column(name = "rate_reset_seconds", nullable = false)
    private Long rateResetSeconds;

    public TokenLimitation() {
    }

    public TokenLimitation(TokenLimitation tokenLimitation) {
        this.id = tokenLimitation.getId();
        this.rateLimit = tokenLimitation.rateLimit;
        this.rateResetSeconds = tokenLimitation.rateResetSeconds;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Long getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(Long rateLimit) {
        this.rateLimit = rateLimit;
    }

    public Long getRateResetSeconds() {
        return rateResetSeconds;
    }

    public void setRateResetSeconds(Long rateResetSeconds) {
        this.rateResetSeconds = rateResetSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TokenLimitation that = (TokenLimitation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TokenLimitation.class.getSimpleName() + "[", "]")
            .add("token='" + id + "'")
            .toString();
    }

    public TokenLimitation copy() {
        return new TokenLimitation(this);
    }
}
