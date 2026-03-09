package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import org.joda.time.DateTime;

import java.util.Objects;

@Entity(name = "MinigamesAccountActivity")
@Table(name = "minigames_account_activity")
public class MinigamesAccountActivity implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "minigames_account_activity_id_seq_generator";
    public static final String ID_SEQ = "minigames_account_activity_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_account_id", nullable = false)
    private GameAccount gameAccount;

    @Column(name = "activity_at")
    private DateTime activityAt;

    @Column(name = "type", length = 64)
    @Enumerated(EnumType.STRING)
    private MinigamesAccountActivityType type;

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

    public MinigamesAccountActivity setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }

    public DateTime getActivityAt() {
        return activityAt;
    }

    public MinigamesAccountActivity setActivityAt(DateTime activityAt) {
        this.activityAt = activityAt;
        return this;
    }

    public MinigamesAccountActivityType getType() {
        return type;
    }

    public MinigamesAccountActivity setType(MinigamesAccountActivityType type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MinigamesAccountActivity that = (MinigamesAccountActivity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MinigamesAccountActivity{");
        sb.append("id=").append(id);
        sb.append(", gameAccount=").append(gameAccount);
        sb.append(", activityAt=").append(activityAt);
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
