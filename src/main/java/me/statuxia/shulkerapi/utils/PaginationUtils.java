package me.statuxia.shulkerapi.utils;

import me.statuxia.shulkerapi.request.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtils {

    private PaginationUtils() {
    }

    public static Pageable convert(PaginationRequest request) {
        return PageRequest.of(
            request.getPage(),
            request.getSize(),
            request.getDirection(),
            "id"
        );
    }
}
