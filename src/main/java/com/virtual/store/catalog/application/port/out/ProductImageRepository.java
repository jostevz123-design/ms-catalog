package com.virtual.store.catalog.application.port.out;

import com.virtual.store.catalog.domain.model.ProductImage;

import java.util.List;

public interface ProductImageRepository {
    ProductImage createProductImage(ProductImage productImage);
    List<ProductImage> getProductImageByProductId(Long productId);
    List<ProductImage> getProductImageByProductVariantId(Long productVariantId);
    void deleteProductImage(Long idProductImage);
    void updateProductImage(ProductImage productImage);
}
