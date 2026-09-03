package com.virtual.store.catalog.adapter.out.persistence.projection;

public interface ProductCatalogProjection {
    Long getId();
    String getName();
    String getBrand();
    Long getMinPrice();
}
