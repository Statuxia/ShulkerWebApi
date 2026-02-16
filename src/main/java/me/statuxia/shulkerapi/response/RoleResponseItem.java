package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleResponseItem {

    private Long id;

    private String roleId;

    private String name;

    private String color;

    private Long discordId;

    private String luckpermsPermission;

    private Boolean availableForTwink;

    public Long getId() {
        return id;
    }

    public RoleResponseItem setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRoleId() {
        return roleId;
    }

    public RoleResponseItem setRoleId(String roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getName() {
        return name;
    }

    public RoleResponseItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getColor() {
        return color;
    }

    public RoleResponseItem setColor(String color) {
        this.color = color;
        return this;
    }

    public Long getDiscordId() {
        return discordId;
    }

    public RoleResponseItem setDiscordId(Long discordId) {
        this.discordId = discordId;
        return this;
    }

    public String getLuckpermsPermission() {
        return luckpermsPermission;
    }

    public RoleResponseItem setLuckpermsPermission(String luckpermsPermission) {
        this.luckpermsPermission = luckpermsPermission;
        return this;
    }

    public Boolean getAvailableForTwink() {
        return availableForTwink;
    }

    public RoleResponseItem setAvailableForTwink(Boolean availableForTwink) {
        this.availableForTwink = availableForTwink;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoleItem{");
        sb.append("id=").append(id);
        sb.append(", roleId='").append(roleId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", color='").append(color).append('\'');
        sb.append(", discordId=").append(discordId);
        sb.append(", luckpermsPermission='").append(luckpermsPermission).append('\'');
        sb.append(", availableForTwink=").append(availableForTwink);
        sb.append('}');
        return sb.toString();
    }
}
