package com.virtual.store.catalog.application.port.out;

import com.virtual.store.catalog.domain.model.Category;

import java.util.List;

public interface CategoryRepository {
    Category createCategory(String nameCategory);
    List<Category> getAllCategories();
    void updateCategory(Category category);
}
