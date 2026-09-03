package com.virtual.store.catalog.adapter.out.persistence.mapper;

import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductImageEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductVariantEntity;
import com.virtual.store.catalog.domain.model.ProductImage;

public class ProductImageMapper {

    private ProductImageMapper(){
        //This class should not be instantiated
    }

    public static ProductImageEntity toEntity(ProductImage productImage, ProductEntity productEntity, ProductVariantEntity productVariantEntity){
        return new ProductImageEntity(
                productImage.url(),
                productImage.isPrimary(),
                productEntity,
                productVariantEntity
        );
    }

    public static ProductImage toDomain(ProductImageEntity productImageEntity){

        Long productVariantId = productImageEntity.getProductVariantEntity() != null
                ? productImageEntity.getProductVariantEntity().getId()
                : null;

        return new ProductImage(
                productImageEntity.getId(),
                productImageEntity.getProductEntity().getId(),
                productVariantId,
                productImageEntity.getUrlImage(),
                productImageEntity.isPrimary()
        );
    }
}
