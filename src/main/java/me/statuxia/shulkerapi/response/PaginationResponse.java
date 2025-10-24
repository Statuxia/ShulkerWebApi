package me.statuxia.shulkerapi.response;

import java.util.List;

public abstract class PaginationResponse<T> {

    private long total;
    private List<T> items;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
