package me.statuxia.shulkerapi.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.joda.time.DateTime;

import java.util.Objects;

@Entity(name = "FineLog")
@Table(name = "fine_log")
public class FineLog implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "fine_log_id_seq_generator";
    public static final String ID_SEQ = "fine_log_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;


    @ManyToOne(optional = false, targetEntity = Fine.class)
    @JoinColumn(name = "fine_id")
    private Fine fine;

    @Column(name = "action_by", nullable = false)
    private String actionBy;

    @Column(name = "fine_action", nullable = false)
    @Enumerated(EnumType.STRING)
    private FineAction action;

    @Column(name = "fine_data")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode data;

    @Column(name = "log_date", nullable = false)
    private DateTime logDate;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Fine getFine() {
        return fine;
    }

    public void setFine(Fine fine) {
        this.fine = fine;
    }

    public String getActionBy() {
        return actionBy;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }

    public FineAction getAction() {
        return action;
    }

    public void setAction(FineAction action) {
        this.action = action;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    public DateTime getLogDate() {
        return logDate;
    }

    public void setLogDate(DateTime logDate) {
        this.logDate = logDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FineLog fineLog = (FineLog) o;
        return Objects.equals(id, fineLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FineLog{");
        sb.append("id=").append(id);
        sb.append(", fine=").append(fine);
        sb.append(", actionBy='").append(actionBy).append('\'');
        sb.append(", action=").append(action);
        sb.append(", data=").append(data);
        sb.append(", logDate=").append(logDate);
        sb.append('}');
        return sb.toString();
    }
}
