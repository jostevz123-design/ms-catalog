package com.virtual.store.catalog.application.port.in;

import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;

public interface ProductUseCase {
    Product createProduct(String name, String description, String brand, Long categoryId);
    PagedResult<Product> getAllProducts(int pageNumber);
    PagedResult<Product> getProductsByCategoryId(Long categoryId, int pageNumber);
    void updateProduct(Long id, String name, String description, String brand);
    void changeActiveStatus(Long productId, boolean active);
}
