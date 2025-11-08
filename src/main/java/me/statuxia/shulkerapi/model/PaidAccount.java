package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;

@Entity(name = "PaidAccount")
@Table(name = "paid_account")
public class PaidAccount {


    public static final String ID_SEQ_GENERATOR = "paid_account_id_seq_generator";
    public static final String ID_SEQ = "paid_account_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    /**
     * Для поддержки множественной покупки мы не валидируем на уникальность
     */
    @Column(nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
