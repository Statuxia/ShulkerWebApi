package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "GameAccount")
@Table(name = "game_account")
public class GameAccount implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "game_account_id_seq_generator";
    public static final String ID_SEQ = "game_account_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "discord_id")
    private DiscordAccount discordAccount;

    private boolean paid;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiscordAccount getDiscordAccount() {
        return discordAccount;
    }

    public void setDiscordAccount(DiscordAccount discordAccount) {
        this.discordAccount = discordAccount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GameAccount that = (GameAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GameAccount{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", discordAccount=").append(discordAccount);
        sb.append(", paid=").append(paid);
        sb.append('}');
        return sb.toString();
    }
}
