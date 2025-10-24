package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import org.joda.time.DateTime;

import java.util.Objects;

@Entity(name = "Fine")
@Table(name = "fine")
public class Fine implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "fine_id_seq_generator";
    public static final String ID_SEQ = "fine_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne(targetEntity = GameAccount.class)
    @JoinColumn(name = "game_account_id", nullable = false)
    private GameAccount gameAccount;

    @Column(name = "fine_value", nullable = false)
    private Long fineValue;

    @Column(nullable = false)
    private String message;

    @Column(name = "create_date", nullable = false)
    private DateTime createDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FineStatus status;

    @Column(name = "status_date", nullable = false)
    private DateTime statusDate;

    @Column(name = "due_date", nullable = false)
    private DateTime dueDate;

    @Column(name = "action_by", nullable = false)
    private String actionBy;

    @Column(nullable = false)
    private Boolean notified;

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

    public Long getFineValue() {
        return fineValue;
    }

    public void setFineValue(Long fineValue) {
        this.fineValue = fineValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    public FineStatus getStatus() {
        return status;
    }

    public void setStatus(FineStatus status) {
        this.status = status;
    }

    public DateTime getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(DateTime statusDate) {
        this.statusDate = statusDate;
    }

    public DateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(DateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getActionBy() {
        return actionBy;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }

    public Boolean isNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public boolean isStatusFinal() {
        final FineStatus fineStatus = getStatus();
        return fineStatus == FineStatus.PAYED || fineStatus == FineStatus.CLOSED;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Fine fine = (Fine) o;
        return Objects.equals(id, fine.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Fine{");
        sb.append("id=").append(id);
        sb.append(", gameAccount=").append(gameAccount);
        sb.append(", fineValue=").append(fineValue);
        sb.append(", message='").append(message).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", status=").append(status);
        sb.append(", statusDate=").append(statusDate);
        sb.append(", dueDate=").append(dueDate);
        sb.append(", actionBy='").append(actionBy).append('\'');
        sb.append(", notified=").append(notified);
        sb.append('}');
        return sb.toString();
    }
}
