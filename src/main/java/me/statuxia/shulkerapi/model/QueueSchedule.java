package me.statuxia.shulkerapi.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import me.statuxia.shulkerapi.converter.JsonNodeConverter;
import org.joda.time.DateTime;

import java.util.Objects;

@Entity(name = "QueueSchedule")
@Table(name = "queue_schedules")
public class QueueSchedule implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "queue_schedules_id_seq_generator";
    public static final String ID_SEQ = "queue_schedules_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "create_date", nullable = false)
    private DateTime createDate;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    @Column(name = "complete_date")
    private DateTime completeDate;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "data", columnDefinition = "jsonb")
    private JsonNode data;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public DateTime getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(DateTime completeDate) {
        this.completeDate = completeDate;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QueueSchedule that = (QueueSchedule) o;
        return completed == that.completed &&
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(createDate, that.createDate) &&
            Objects.equals(completeDate, that.completeDate) &&
            Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, createDate, completed, completeDate, data);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QueueSchedule{");
        sb.append("id=").append(id);
        sb.append(", type='").append(type).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", completed=").append(completed);
        sb.append(", completeDate=").append(completeDate);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}