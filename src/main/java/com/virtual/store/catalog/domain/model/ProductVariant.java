package com.virtual.store.catalog.domain.model;

import java.util.Map;

public record ProductVariant(Long id, Long productId, String sku, Map<String, String> attributes, Long price, Long stock) {
}
