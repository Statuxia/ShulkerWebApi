package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCardMemberPaginationResponse extends PaginationResponse<GroupCardMemberItem> {
}
