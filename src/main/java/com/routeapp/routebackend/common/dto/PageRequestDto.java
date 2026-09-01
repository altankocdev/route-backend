package com.routeapp.routebackend.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PageRequestDto(
        Integer page,
        Integer size,
        String sortBy,
        Sort.Direction direction
) {
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    public PageRequestDto {
        page = (page != null && page >= 0) ? page : 0;
        size = (size != null && size > 0) ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        sortBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt";
        direction = (direction != null) ? direction : Sort.Direction.DESC;
    }

    public static PageRequestDto defaultRequest() {
        return new PageRequestDto(null, null, null, null);
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}