package com.virtual.store.catalog.adapter.in.web.dto;

import com.virtual.store.catalog.domain.model.ProductCatalogItem;

public record ProductCatalogResponse(Long id, String name, String brand, Long price, String imageUrl) {
    public static ProductCatalogResponse from(ProductCatalogItem product){
        return new ProductCatalogResponse(
                product.id(),
                product.name(),
                product.brand(),
                product.price(),
                product.imageUrl()
        );
    }
}
