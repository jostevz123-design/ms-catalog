package com.virtual.store.catalog.adapter.in.web.dto;

import java.util.List;

public record PageResultResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        Long totalElements,
        int totalPages
) { }
