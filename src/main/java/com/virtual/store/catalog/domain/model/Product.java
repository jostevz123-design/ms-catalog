package com.virtual.store.catalog.domain.model;

public record Product(Long id, String name, String description, String brand, boolean isActive, Long idCategory) {
    public Product(Long id, String name, String description, String brand){
        this(id, name, description, brand, true, null);
    }
}
