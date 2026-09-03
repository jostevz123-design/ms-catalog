package com.virtual.store.catalog.adapter.out.persistence.mapper;

import com.virtual.store.catalog.adapter.out.persistence.projection.ProductCatalogProjection;
import com.virtual.store.catalog.domain.model.ProductCatalogItem;

public class ProductCatalogMapper {

    private ProductCatalogMapper(){
        //This class should not be instantiated
    }

    public static ProductCatalogItem toDomain(ProductCatalogProjection productCatalogProjection, String imageUrl){
        return new ProductCatalogItem(
                productCatalogProjection.getId(),
                productCatalogProjection.getName(),
                productCatalogProjection.getBrand(),
                productCatalogProjection.getMinPrice(),
                imageUrl
        );
    }
}
