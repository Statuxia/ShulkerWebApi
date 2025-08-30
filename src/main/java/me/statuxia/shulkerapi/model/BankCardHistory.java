package me.statuxia.shulkerapi.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.joda.time.DateTime;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "BankCardHistory")
@Table(name = "bank_card_history")
public class BankCardHistory implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "bank_card_history_id_seq_generator";
    public static final String ID_SEQ = "bank_card_history_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @Column(name = "history_uuid", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID uuid;

    @ManyToOne(targetEntity = BankCard.class, optional = false)
    @JoinColumn(name = "bank_card_id")
    private BankCard card;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private BankCardHistoryType type;

    @Column(name = "create_time", nullable = false)
    private DateTime createTime;

    @Column(name = "history_data")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode historyData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public BankCard getCard() {
        return card;
    }

    public void setCard(BankCard card) {
        this.card = card;
    }

    public BankCardHistoryType getType() {
        return type;
    }

    public void setType(BankCardHistoryType type) {
        this.type = type;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public JsonNode getHistoryData() {
        return historyData;
    }

    public void setHistoryData(JsonNode historyData) {
        this.historyData = historyData;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BankCardHistory that = (BankCardHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, card, type, createTime, historyData);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BankCardHistory{");
        sb.append("id=").append(id);
        sb.append(", uuid=").append(uuid);
        sb.append(", card=").append(card);
        sb.append(", type=").append(type);
        sb.append(", createTime=").append(createTime);
        sb.append(", historyData=").append(historyData);
        sb.append('}');
        return sb.toString();
    }
}
