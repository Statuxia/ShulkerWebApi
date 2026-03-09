package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import org.joda.time.DateTime;

import java.util.Objects;

@Entity(name = "MinigamesAccountSession")
@Table(name = "minigames_account_session")
public class MinigamesAccountSession implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "minigames_account_session_id_seq_generator";
    public static final String ID_SEQ = "minigames_account_session_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "game_account_id", nullable = false)
    private GameAccount gameAccount;

    @Column(name = "playtime")
    private Long playtime;

    @Column(name = "first_join")
    private DateTime firstJoin;

    @Column(name = "last_join")
    private DateTime lastJoin;

    @Column(name = "online")
    private Boolean online;

    @Column(name = "last_online")
    private DateTime lastOnline;

    @Column(name = "updated_at")
    private DateTime updatedAt;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public GameAccount getGameAccount() {
        return gameAccount;
    }

    public MinigamesAccountSession setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }

    public Long getPlaytime() {
        return playtime;
    }

    public MinigamesAccountSession setPlaytime(Long playtime) {
        this.playtime = playtime;
        return this;
    }

    public DateTime getFirstJoin() {
        return firstJoin;
    }

    public MinigamesAccountSession setFirstJoin(DateTime firstJoin) {
        this.firstJoin = firstJoin;
        return this;
    }

    public DateTime getLastJoin() {
        return lastJoin;
    }

    public MinigamesAccountSession setLastJoin(DateTime lastJoin) {
        this.lastJoin = lastJoin;
        return this;
    }

    public Boolean getOnline() {
        return online;
    }

    public MinigamesAccountSession setOnline(Boolean online) {
        this.online = online;
        return this;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public MinigamesAccountSession setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public DateTime getLastOnline() {
        return lastOnline;
    }

    public MinigamesAccountSession setLastOnline(DateTime lastOnline) {
        this.lastOnline = lastOnline;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MinigamesAccountSession that = (MinigamesAccountSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MinigamesAccountSession{");
        sb.append("id=").append(id);
        sb.append(", gameAccount=").append(gameAccount);
        sb.append(", playtime=").append(playtime);
        sb.append(", firstJoin=").append(firstJoin);
        sb.append(", lastJoin=").append(lastJoin);
        sb.append(", online=").append(online);
        sb.append(", lastOnline=").append(lastOnline);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }
}
