package com.virtual.store.catalog.adapter.out.persistence.mapper;

import com.virtual.store.catalog.adapter.out.persistence.entity.CategoryEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.domain.model.Product;

public class ProductMapper {

    private ProductMapper(){
        //This class should not be instantiated
    }

    public static ProductEntity toEntity(Product product, CategoryEntity categoryEntity){
        return new ProductEntity(product.name(), product.description(), product.brand(), product.isActive(), categoryEntity);
    }

    public static Product toDomain(ProductEntity productEntity){
        return new Product(productEntity.getId(), productEntity.getName(), productEntity.getDescription(), productEntity.getBrand(), productEntity.isActive(), productEntity.getCategoryEntity().getId());
    }
}
