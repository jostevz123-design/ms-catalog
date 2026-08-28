package com.virtual.store.catalog.adapter.out.persistence.mapper;

import com.virtual.store.catalog.adapter.out.persistence.entity.CategoryEntity;
import com.virtual.store.catalog.domain.model.Category;

public class CategoryMapper {

    public static CategoryEntity toToEntity(String name){
        return new CategoryEntity(name);
    }

    public static Category ToDomain(CategoryEntity categoryEntity){
        return new Category(categoryEntity.getId(), categoryEntity.getName());
    }
}
