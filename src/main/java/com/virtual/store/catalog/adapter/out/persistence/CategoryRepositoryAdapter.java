package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.CategoryEntity;
import com.virtual.store.catalog.adapter.out.persistence.mapper.CategoryMapper;
import com.virtual.store.catalog.application.port.out.CategoryRepository;
import com.virtual.store.catalog.domain.exception.ResourceNotFoundException;
import com.virtual.store.catalog.domain.model.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryRepositoryAdapter(CategoryJpaRepository categoryJpaRepository){
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public Category createCategory(String nameCategory) {
        CategoryEntity categoryEntity = CategoryMapper.toToEntity(nameCategory);
        categoryJpaRepository.save(categoryEntity);
        return CategoryMapper.toDomain(categoryEntity);
    }

    @Override
    public List<Category> getAllCategories() {
        List<CategoryEntity> categoryEntityList = categoryJpaRepository.findAll();
        return categoryEntityList.stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }

    @Override
    public void updateCategory(Category category) {
        CategoryEntity categoryEntity = categoryJpaRepository.findById(category.id())
                .orElseThrow(() -> new ResourceNotFoundException("Category with id:" + category.id() + " does not exist"));
        categoryEntity.setName(category.name());
    }
}
