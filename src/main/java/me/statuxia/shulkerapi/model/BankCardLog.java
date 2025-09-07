package me.statuxia.shulkerapi.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.joda.time.DateTime;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "BankCardLog")
@Table(name = "bank_card_log")
public class BankCardLog implements Identifiable<Long> {
    public static final String ID_SEQ_GENERATOR = "bank_card_log_id_seq_generator";
    public static final String ID_SEQ = "bank_card_log_id_seq";

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

    @Column(name = "action_by", nullable = false)
    private String actionBy;

    @Column(name = "action_time", nullable = false)
    private DateTime actionTime;

    @Column(name = "log_data")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode data;

    @Override
    public Long getId() {
        return id;
    }

    @Override
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

    public String getActionBy() {
        return actionBy;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }

    public DateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(DateTime actionTime) {
        this.actionTime = actionTime;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BankCardLog that = (BankCardLog) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BankCardLog{");
        sb.append("id=").append(id);
        sb.append(", uuid=").append(uuid);
        sb.append(", card=").append(card);
        sb.append(", actionBy='").append(actionBy).append('\'');
        sb.append(", actionTime=").append(actionTime);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
