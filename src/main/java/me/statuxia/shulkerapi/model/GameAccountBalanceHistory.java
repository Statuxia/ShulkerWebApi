package me.statuxia.shulkerapi.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.joda.time.DateTime;

@Entity(name = "GameAccountBalanceHistory")
@Table(name = "game_account_balance_history")
public class GameAccountBalanceHistory {

    public static final String ID_SEQ_GENERATOR = "game_account_balance_history_id_seq_generator";
    public static final String ID_SEQ = "game_account_balance_history_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne(targetEntity = GameAccountBalance.class, optional = false)
    @JoinColumn(name = "game_account_balance")
    private GameAccountBalance gameAccountBalance;

    @ManyToOne(targetEntity = GameAccount.class, optional = false)
    @JoinColumn(name = "game_account_id")
    private GameAccount gameAccount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameAccountBalanceType type;

    @Column(name = "data")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode data;

    @Column(name = "create_date", nullable = false)
    private DateTime createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameAccountBalance getGameAccountBalance() {
        return gameAccountBalance;
    }

    public void setGameAccountBalance(GameAccountBalance gameAccountBalance) {
        this.gameAccountBalance = gameAccountBalance;
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

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GameAccountBalanceHistory{");
        sb.append("id=").append(id);
        sb.append(", gameAccountBalance=").append(gameAccountBalance);
        sb.append(", gameAccount=").append(gameAccount);
        sb.append(", type=").append(type);
        sb.append(", data=").append(data);
        sb.append(", createDate=").append(createDate);
        sb.append('}');
        return sb.toString();
    }
}
