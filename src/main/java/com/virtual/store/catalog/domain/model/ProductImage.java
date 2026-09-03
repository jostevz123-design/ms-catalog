package com.virtual.store.catalog.domain.model;

public record ProductImage(Long id, Long productId, Long productVariantId, String url, boolean isPrimary) {
}
