package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "BankCardMemberSetting")
@Table(name = "bank_card_member_setting")
public class BankCardMemberSetting implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "bank_card_member_setting_id_seq_generator";
    public static final String ID_SEQ = "bank_card_member_setting_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne(targetEntity = BankCard.class)
    @JoinColumn(name = "bank_card_id", nullable = false)
    private BankCard card;

    @ManyToOne(targetEntity = BankCardMember.class)
    @JoinColumn(name = "bank_card_member_id", nullable = false)
    private BankCardMember bankCardMember;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BankCardSettingType type;

    @Column
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

    public BankCardMember getBankCardMember() {
        return bankCardMember;
    }

    public void setBankCardMember(BankCardMember bankCardMember) {
        this.bankCardMember = bankCardMember;
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
        final BankCardMemberSetting that = (BankCardMemberSetting) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BankCardMemberSetting{");
        sb.append("id=").append(id);
        sb.append(", card=").append(card);
        sb.append(", bankCardMember=").append(bankCardMember);
        sb.append(", type=").append(type);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
