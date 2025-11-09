package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;
import org.joda.time.DateTime;

@Entity(name = "GameSessionIp")
@Table(name = "game_session_ip")
public class GameSessionIp implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "game_session_ip_id_seq_generator";
    public static final String ID_SEQ = "game_session_ip_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_account_id")
    private GameAccount gameAccount;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false, name = "last_join_date")
    private DateTime lastJoinDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameSessionIpState state;

    @Column(nullable = false)
    private boolean notified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameAccount getGameAccount() {
        return gameAccount;
    }

    public void setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public DateTime getLastJoinDate() {
        return lastJoinDate;
    }

    public void setLastJoinDate(DateTime lastJoinDate) {
        this.lastJoinDate = lastJoinDate;
    }

    public GameSessionIpState getState() {
        return state;
    }

    public void setState(GameSessionIpState state) {
        this.state = state;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }
}
