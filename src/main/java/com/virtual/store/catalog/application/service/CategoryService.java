package com.virtual.store.catalog.application.service;

import com.virtual.store.catalog.application.port.in.CategoryUseCase;
import com.virtual.store.catalog.application.port.out.CategoryRepository;
import com.virtual.store.catalog.domain.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService implements CategoryUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category createCategory(String name) {
        //TODO implement and exposed in controller in private path
        Category created = categoryRepository.createCategory(name);
        logger.info("Category created successfully with: id={}, name={}",created.id(), created.name());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    @Override
    @Transactional
    public void updateCategoryName(Category category) {
        //TODO implement and exposed in controller in private path
        logger.info("Updating Category with id={}", category.id());
        categoryRepository.updateCategory(category);
        logger.info("Category Updated with id={}", category.id());
    }
}
