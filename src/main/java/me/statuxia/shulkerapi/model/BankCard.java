package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import org.joda.time.DateTime;

import java.util.Objects;

@Entity(name = "BankCard")
@Table(name = "bank_card")
public class BankCard implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "bank_card_id_seq_generator";
    public static final String ID_SEQ = "bank_card_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @Column(name = "number", nullable = false, unique = true)
    @Pattern(regexp = "\\d{4} \\d{4}")
    private String number;

    @ManyToOne(targetEntity = GameAccount.class)
    @JoinColumn(name = "game_account_id")
    private GameAccount gameAccount;

    @Column(nullable = false)
    @Pattern(regexp = "\\d{4}")
    private String pin;

    @Column(nullable = false)
    private Long currency = 0L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType type;

    @Column(name = "create_time", nullable = false)
    private DateTime createTime;

    private boolean disabled;

    @Column(name = "disabled_time")
    private DateTime disabledTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public GameAccount getGameAccount() {
        return gameAccount;
    }

    public void setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Long getCurrency() {
        return currency;
    }

    public void setCurrency(Long currency) {
        this.currency = currency;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public DateTime getDisabledTime() {
        return disabledTime;
    }

    public void setDisabledTime(DateTime disabledTime) {
        this.disabledTime = disabledTime;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BankCard bankCard = (BankCard) o;
        return Objects.equals(id, bankCard.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BankCard{");
        sb.append("id=").append(id);
        sb.append(", number=").append(number);
        sb.append(", gameAccount=").append(gameAccount);
        sb.append(", pin=").append(pin);
        sb.append(", currency=").append(currency);
        sb.append(", type=").append(type);
        sb.append(", createTime=").append(createTime);
        sb.append(", disabled=").append(disabled);
        sb.append(", disabledTime=").append(disabledTime);
        sb.append('}');
        return sb.toString();
    }
}
