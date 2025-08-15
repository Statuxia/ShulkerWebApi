package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import org.joda.time.DateTime;

import java.util.Objects;
import java.util.StringJoiner;

@Entity(name = "DiscordAccount")
@Table(name = "discord_account")
public class DiscordAccount implements Identifiable<Long>, DisableAware, TokenSource {

    @Id
    private Long id;

    /**
     * Внутренний токен, через который можно обращаться к API
     */
    @Column(name = "session_token")
    private String sessionToken;

    /**
     * Токен доступа получения данных об аккаунте
     */
    @Column(name = "access_token", nullable = false)
    private String accessToken;

    /**
     * Токен для обновления токена доступа
     */
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    /**
     * Время, после которого нужно обновить токен
     * Discord API дает 7 дней, мы обновляем токены старше 6 дней
     */
    @Column(name = "update_time", nullable = false)
    private DateTime updateTime;

    private boolean disabled;

    @Column(name = "disabled_time")
    private DateTime disabledTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public DateTime getDisabledTime() {
        return disabledTime;
    }

    @Override
    public void setDisabledTime(DateTime dateTime) {
        this.disabledTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DiscordAccount that = (DiscordAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DiscordAccount.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .toString();
    }
}
