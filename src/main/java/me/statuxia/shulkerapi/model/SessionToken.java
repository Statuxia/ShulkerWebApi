package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import org.joda.time.DateTime;

import java.util.Objects;

@Entity(name = "SessionToken")
@Table(name = "session_token")
public class SessionToken implements Identifiable<Long>, DisableAware, TokenSource {

    public static final String ID_SEQ_GENERATOR = "session_token_id_seq_generator";
    public static final String ID_SEQ = "session_token_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(name = "create_time", nullable = false)
    private DateTime createTime;

    private boolean disabled;

    @Column(name = "disabled_time")
    private DateTime disabledTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public DateTime getDisabledTime() {
        return disabledTime;
    }

    public void setDisabledTime(DateTime disabledTime) {
        this.disabledTime = disabledTime;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SessionToken that = (SessionToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SessionToken{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
