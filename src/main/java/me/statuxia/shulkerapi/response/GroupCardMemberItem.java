package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCardMemberItem {

    private Long id;
    private String gameAccount;
    private Long addedAt;
    private Long credited;
    private Long debited;

    public Long getId() {
        return id;
    }

    public GroupCardMemberItem setId(Long id) {
        this.id = id;
        return this;
    }

    public String getGameAccount() {
        return gameAccount;
    }

    public GroupCardMemberItem setGameAccount(String gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }

    public Long getAddedAt() {
        return addedAt;
    }

    public GroupCardMemberItem setAddedAt(Long addedAt) {
        this.addedAt = addedAt;
        return this;
    }

    public Long getCredited() {
        return credited;
    }

    public GroupCardMemberItem setCredited(Long credited) {
        this.credited = credited;
        return this;
    }

    public Long getDebited() {
        return debited;
    }

    public GroupCardMemberItem setDebited(Long debited) {
        this.debited = debited;
        return this;
    }
}
