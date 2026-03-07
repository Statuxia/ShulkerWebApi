package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "Role")
@Table(name = "roles")
public class Role implements Identifiable<Long> {

    public static final String ID_SEQ_GENERATOR = "roles_id_seq_generator";
    public static final String ID_SEQ = "roles_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ_GENERATOR)
    @SequenceGenerator(name = ID_SEQ_GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @Column(name = "role_id", nullable = false)
    private String roleId;

    @Column(nullable = false)
    private String name;

    private String color;

    @Column(name = "discord_id", nullable = false)
    private Long discordId;

    @Column(name = "luckperms_permission")
    private String luckpermsPermission;

    @Column(name = "available_for_twink", nullable = false)
    private Boolean availableForTwink;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(Long discordId) {
        this.discordId = discordId;
    }

    public String getLuckpermsPermission() {
        return luckpermsPermission;
    }

    public void setLuckpermsPermission(String luckpermsPermission) {
        this.luckpermsPermission = luckpermsPermission;
    }

    public Boolean getAvailableForTwink() {
        return availableForTwink;
    }

    public void setAvailableForTwink(Boolean availableForTwink) {
        this.availableForTwink = availableForTwink;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Role role = (Role) o;
        return Objects.equals(id, role.id)
            && Objects.equals(roleId, role.roleId)
            && Objects.equals(name, role.name)
            && Objects.equals(color, role.color)
            && Objects.equals(discordId, role.discordId)
            && Objects.equals(luckpermsPermission, role.luckpermsPermission)
            && Objects.equals(availableForTwink, role.availableForTwink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleId, name, color, discordId, luckpermsPermission, availableForTwink);
    }

    @Override
    public String toString() {
        return "Role{"
            + "id=" + id
            + ", roleId='" + roleId + '\''
            + ", name='" + name + '\''
            + ", color='" + color + '\''
            + ", discordId=" + discordId
            + ", luckpermsPermission='" + luckpermsPermission
            + '\''
            + ", availableForTwink=" + availableForTwink
            + '}';
    }
}
