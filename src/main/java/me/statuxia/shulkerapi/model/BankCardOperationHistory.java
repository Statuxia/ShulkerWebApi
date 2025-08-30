package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.joda.time.DateTime;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "BankCardOperationHistory")
@Table(name = "bank_card_operation_history")
public class BankCardOperationHistory implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "bank_card_operation_history_id_seq_generator";
    public static final String ID_SEQ = "bank_card_operation_history_id_seq";

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
    private BankOperationState state;

    @Column(name = "create_time", nullable = false)
    private DateTime createTime;

    @Column(nullable = false)
    @Min(1)
    private Integer value;

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

    public BankOperationState getState() {
        return state;
    }

    public void setState(BankOperationState state) {
        this.state = state;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BankCardOperationHistory that = (BankCardOperationHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BankCardOperationHistory{");
        sb.append("id=").append(id);
        sb.append(", uuid=").append(uuid);
        sb.append(", card=").append(card);
        sb.append(", state=").append(state);
        sb.append(", createTime=").append(createTime);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
