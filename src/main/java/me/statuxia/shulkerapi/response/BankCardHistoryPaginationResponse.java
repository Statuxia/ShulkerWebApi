package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankCardHistoryPaginationResponse extends PaginationResponse<BankCardHistoryItem> {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BankCardHistoryPaginationResponse that = (BankCardHistoryPaginationResponse) o;
        return getTotal() == that.getTotal() && Objects.equals(getItems(), that.getItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTotal(), getItems());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PaginationResponse{");
        sb.append("total=").append(getTotal());
        sb.append(", items=").append(getItems());
        sb.append('}');
        return sb.toString();
    }
}
