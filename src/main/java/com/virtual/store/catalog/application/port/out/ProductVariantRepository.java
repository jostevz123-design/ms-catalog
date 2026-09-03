package com.virtual.store.catalog.application.port.out;

import com.virtual.store.catalog.domain.model.ProductVariant;

import java.util.List;

public interface ProductVariantRepository {
    ProductVariant createProductVariant(ProductVariant productVariant);
    void updateProductVariant(ProductVariant productVariant);
    void updateVariantStock(Long variantId, Long stock);
    List<ProductVariant> getVariantsByProductId(Long productId);
}
