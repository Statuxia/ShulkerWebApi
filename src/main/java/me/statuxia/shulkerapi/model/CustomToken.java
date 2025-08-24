package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import org.joda.time.DateTime;

import java.util.Objects;
import java.util.StringJoiner;

@Entity(name = "CustomToken")
@Table(name = "custom_token")
public class CustomToken implements Identifiable<Long>, DisableAware, TokenSource {

    public static final String ID_SEQ_GENERATOR = "custom_token_id_seq_generator";
    public static final String ID_SEQ = "custom_token_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String token;

    private boolean disabled;

    @Column(name = "disabled_time")
    private DateTime disabledTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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
    public void setDisabledTime(DateTime disabledTime) {
        this.disabledTime = disabledTime;
    }

    @Override
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
        final CustomToken that = (CustomToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CustomToken.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .toString();
    }
}
