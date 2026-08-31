package com.virtual.store.catalog.adapter.in.web.dto;

import com.virtual.store.catalog.domain.model.Product;

public record ProductResponse(Long id, String name, String description, String brand, boolean active, Long categoryId) {
    public static ProductResponse from(Product product){
        return new ProductResponse(
                product.id(),
                product.name(),
                product.description(),
                product.brand(),
                product.isActive(),
                product.idCategory()
        );
    }
}
