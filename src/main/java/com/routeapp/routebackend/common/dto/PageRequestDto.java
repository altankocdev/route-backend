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
    public PageRequestDto {
        page = (page != null) ? page : 0;
        size = (size != null) ? size : 20;
        sortBy = (sortBy != null) ? sortBy : "createdAt";
        direction = (direction != null) ? direction : Sort.Direction.DESC;
    }

    public static PageRequestDto defaultRequest() {
        return new PageRequestDto(null, null, null, null);
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}