package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;

@Entity(name = "GameAccountBalance")
@Table(name = "game_account_balance")
public class GameAccountBalance implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "game_account_balance_id_seq_generator";
    public static final String ID_SEQ = "game_account_balance_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne(targetEntity = GameAccount.class, optional = false)
    @JoinColumn(name = "game_account_id")
    private GameAccount gameAccount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameAccountBalanceType type;

    @Column(nullable = false)
    private Long value;

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

    public void setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
    }

    public GameAccountBalanceType getType() {
        return type;
    }

    public void setType(GameAccountBalanceType type) {
        this.type = type;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GameAccountBalance{");
        sb.append("id=").append(id);
        sb.append(", gameAccount=").append(gameAccount);
        sb.append(", type=").append(type);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
