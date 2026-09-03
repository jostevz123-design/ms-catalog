package com.virtual.store.catalog.adapter.out.persistence.mapper;

import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductVariantEntity;
import com.virtual.store.catalog.domain.model.ProductVariant;

public class ProductVariantMapper {

    private ProductVariantMapper(){
        //This class should not be instantiated
    }

    public static ProductVariantEntity toEntity(ProductVariant productVariant, ProductEntity productEntity){
        return new ProductVariantEntity(productVariant.sku(), productVariant.attributes(), productVariant.price(), productVariant.stock(), productEntity);
    }

    public static ProductVariant toDomain(ProductVariantEntity productVariantEntity){
        return new ProductVariant(
                productVariantEntity.getId(),
                productVariantEntity.getProductEntity().getId(),
                productVariantEntity.getSku(),
                productVariantEntity.getAttributes(),
                productVariantEntity.getPrice(),
                productVariantEntity.getStock());
    }
}
