package com.virtual.store.catalog.application.service;

import com.virtual.store.catalog.application.port.out.CategoryRepository;
import com.virtual.store.catalog.domain.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategories_returnsCategoriesFromRepository() {
        List<Category> categories = List.of(
                new Category(1L, "Electronics"),
                new Category(2L, "Clothing")
        );
        when(categoryRepository.getAllCategories()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).containsExactlyElementsOf(categories);
    }

    @Test
    void getAllCategories_returnsEmptyList_whenRepositoryHasNoCategories() {
        when(categoryRepository.getAllCategories()).thenReturn(List.of());

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).isEmpty();
    }

    @Test
    void createCategory_delegatesToRepositoryAndReturnsCreatedCategory() {
        Category created = new Category(1L, "Electronics");
        when(categoryRepository.createCategory("Electronics")).thenReturn(created);

        Category result = categoryService.createCategory("Electronics");

        assertThat(result).isEqualTo(created);
        verify(categoryRepository).createCategory("Electronics");
    }

    @Test
    void updateCategoryName_delegatesToRepository() {
        Category category = new Category(1L, "Updated Name");

        categoryService.updateCategoryName(category);

        verify(categoryRepository).updateCategory(category);
    }
}
