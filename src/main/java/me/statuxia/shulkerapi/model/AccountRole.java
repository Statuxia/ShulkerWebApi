package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import org.joda.time.DateTime;

import java.util.Objects;

@Entity(name = "AccountRole")
@Table(name = "account_roles")
public class AccountRole implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "account_roles_id_seq_generator";
    public static final String ID_SEQ = "account_roles_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "receive_date", nullable = false)
    private DateTime receiveDate;

    @Column(name = "expire_date", nullable = false)
    private DateTime expireDate;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public DateTime getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(DateTime receiveDate) {
        this.receiveDate = receiveDate;
    }

    public DateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(DateTime expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AccountRole that = (AccountRole) o;
        return Objects.equals(id, that.id)
            && Objects.equals(account, that.account)
            && Objects.equals(role, that.role)
            && Objects.equals(receiveDate, that.receiveDate)
            && Objects.equals(expireDate, that.expireDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, role, receiveDate, expireDate);
    }

    @Override
    public String toString() {
        return "AccountRole{"
            + "id=" + id
            + ", account=" + account
            + ", role=" + role
            + ", receiveDate=" + receiveDate
            + ", expireDate=" + expireDate
            + '}';
    }
}
