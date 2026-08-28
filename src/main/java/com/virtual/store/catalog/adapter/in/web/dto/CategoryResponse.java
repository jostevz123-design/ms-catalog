package com.virtual.store.catalog.adapter.in.web.dto;

import com.virtual.store.catalog.domain.model.Category;

public record CategoryResponse(Long id, String name) {
    public static CategoryResponse from(Category category){
        return new CategoryResponse(category.id(), category.name());
    }
}
