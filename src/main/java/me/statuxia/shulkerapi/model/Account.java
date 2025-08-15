package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Связующее звено между интеграциями и игровыми аккаунтами
 * На данный момент это только Discord, но в перспективе можно добавить и Telegram
 */
@Entity(name = "Account")
@Table(name = "account")
public class Account implements Identifiable<Long>, DisableAware {

    public static final String ID_SEQ_GENERATOR = "account_id_seq_generator";
    public static final String ID_SEQ = "account_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "discord_account")
    private DiscordAccount discordAccount;

    @OneToMany(mappedBy = "account")
    private List<CustomToken> customTokens;

    private boolean disabled;

    @Column(name = "disabled_time", nullable = false)
    private DateTime disabledTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public DiscordAccount getDiscordAccount() {
        return discordAccount;
    }

    public void setDiscordAccount(DiscordAccount discordAccount) {
        this.discordAccount = discordAccount;
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

    public List<CustomToken> getCustomTokens() {
        return customTokens;
    }

    public void setCustomTokens(List<CustomToken> customTokens) {
        this.customTokens = customTokens;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Account.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .toString();
    }
}
