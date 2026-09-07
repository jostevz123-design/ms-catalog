package com.virtual.store.catalog.domain.model;

import java.util.List;

public record ProductDetail(
        Product product,
        List<ProductVariant> productVariantList,
        List<ProductImage> productImageList) {
}
