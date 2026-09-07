package com.virtual.store.catalog.adapter.in.web.dto;

import com.virtual.store.catalog.domain.model.ProductVariant;

import java.util.List;
import java.util.Map;

public record ProductVariantResponse(
        Long id,
        String sku,
        Map<String, String> attributes,
        Long price,
        List<String> imageUrls) {

    public static ProductVariantResponse from(ProductVariant productVariant, List<String> imageUrls){
        return new ProductVariantResponse(productVariant.id(), productVariant.sku(), productVariant.attributes(), productVariant.price(), imageUrls);
    }
}
