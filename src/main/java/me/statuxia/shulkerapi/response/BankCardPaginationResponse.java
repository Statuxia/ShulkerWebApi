package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankCardPaginationResponse extends PaginationResponse<BankCardItem> {
}
