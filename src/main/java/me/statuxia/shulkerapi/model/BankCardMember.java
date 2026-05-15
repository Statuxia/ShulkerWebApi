package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Objects;

@Entity(name = "BankCardMember")
@Table(name = "bank_card_member")
public class BankCardMember implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "bank_card_member_id_seq_generator";
    public static final String ID_SEQ = "bank_card_member_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne(targetEntity = BankCard.class)
    @JoinColumn(name = "bank_card_id", nullable = false)
    private BankCard card;

    @ManyToOne(targetEntity = GameAccount.class)
    @JoinColumn(name = "game_account_id", nullable = false)
    private GameAccount gameAccount;

    @Column(nullable = false)
    private String pin;

    @Column(name = "added_at", nullable = false)
    private DateTime addedAt;

    @Column(nullable = false)
    private Long credited = 0L;

    @Column(nullable = false)
    private Long debited = 0L;

    @OneToMany(mappedBy = "bankCardMember", fetch = FetchType.LAZY)
    private List<BankCardMemberSetting> settings;

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

    public DateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(DateTime addedAt) {
        this.addedAt = addedAt;
    }

    public Long getCredited() {
        return credited;
    }

    public void setCredited(Long credited) {
        this.credited = credited;
    }

    public Long getDebited() {
        return debited;
    }

    public void setDebited(Long debited) {
        this.debited = debited;
    }

    public List<BankCardMemberSetting> getSettings() {
        return settings;
    }

    public void setSettings(List<BankCardMemberSetting> settings) {
        this.settings = settings;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BankCardMember that = (BankCardMember) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BankCardMember{");
        sb.append("id=").append(id);
        sb.append(", card=").append(card);
        sb.append(", gameAccount=").append(gameAccount);
        sb.append(", pin=").append(pin);
        sb.append(", addedAt=").append(addedAt);
        sb.append(", credited=").append(credited);
        sb.append(", debited=").append(debited);
        sb.append('}');
        return sb.toString();
    }
}
