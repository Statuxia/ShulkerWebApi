package me.statuxia.shulkerapi.utils;

import me.statuxia.shulkerapi.request.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

public class PaginationUtils {

    private PaginationUtils() {
    }

    public static Pageable convert(PaginationRequest request) {
        return PageRequest.of(
            request.getPage(),
            request.getSize(),
            request.getDirection(),
            StringUtils.hasText(request.getSortProperty()) ? request.getSortProperty() : "id"
        );
    }
}
