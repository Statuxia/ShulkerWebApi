package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.statuxia.shulkerapi.utils.PaginationUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@JsonIgnoreProperties(ignoreUnknown = true)

public class PaginationRequest {

    @NotNull
    private Sort.Direction direction = Sort.Direction.DESC;

    @Min(0)
    private int page;
    @Min(1)
    @Max(100)
    private int size = 20;
    private String sortProperty = "id";

    public Sort.Direction getDirection() {
        return direction;
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    @JsonIgnore
    public Pageable getPageable() {
        return PaginationUtils.convert(this);
    }
}
