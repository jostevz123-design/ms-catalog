package com.virtual.store.catalog.application.port.out;

import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;

import java.util.Set;

public interface ProductRepository {
    Product createProduct (Product product);
    PagedResult<Product> getAllProducts(int pageNumber, int pageSize);
    PagedResult<Product> getProductsByCategoryId(Long categoryId, int pageNumber, int pageSize);
    void updateProduct(Product product);
    void changeActiveStatus(Long productId,boolean active);
}
