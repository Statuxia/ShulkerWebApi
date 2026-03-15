package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "BankCardSetting")
@Table(name = "bank_card_setting")
public class BankCardSetting implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "bank_card_setting_id_seq_generator";
    public static final String ID_SEQ = "bank_card_setting_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne(targetEntity = BankCard.class)
    @JoinColumn(name = "bank_card_id", nullable = false)
    private BankCard card;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BankCardSettingType type;

    @Column(nullable = false)
    private String value;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public BankCard getCard() {
        return card;
    }

    public void setCard(BankCard card) {
        this.card = card;
    }

    public BankCardSettingType getType() {
        return type;
    }

    public void setType(BankCardSettingType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BankCardSetting that = (BankCardSetting) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BankCardSetting{");
        sb.append("id=").append(id);
        sb.append(", card=").append(card);
        sb.append(", type=").append(type);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
