package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCardMemberRemoveRequest extends BaseCardRequest {

    @NotNull
    @NotEmpty
    private String memberGameAccount;

    public String getMemberGameAccount() {
        return memberGameAccount;
    }

    public void setMemberGameAccount(String memberGameAccount) {
        this.memberGameAccount = memberGameAccount;
    }
}
