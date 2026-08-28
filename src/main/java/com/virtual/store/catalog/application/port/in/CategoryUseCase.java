package com.virtual.store.catalog.application.port.in;

import com.virtual.store.catalog.domain.model.Category;

import java.util.List;

public interface CategoryUseCase {
    public Category createCategory(String name);
    List<Category> getAllCategories();
    void updateCategoryName(Category category);
}
