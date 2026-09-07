package com.virtual.store.catalog.application.port.out;

import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;
import com.virtual.store.catalog.domain.model.ProductCatalogItem;

public interface ProductRepository {
    Product createProduct (Product product);
    PagedResult<ProductCatalogItem> getAllProducts(int pageNumber, int pageSize);
    PagedResult<ProductCatalogItem> getProductsByCategoryId(Long categoryId, int pageNumber, int pageSize);
    void updateProduct(Product product);
    void changeActiveStatus(Long productId,boolean active);
    Product getProductById(Long productId);
}
