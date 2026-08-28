package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.CategoryEntity;
import com.virtual.store.catalog.domain.exception.ResourceNotFoundException;
import com.virtual.store.catalog.domain.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(CategoryRepositoryAdapter.class)
class CategoryRepositoryAdapterTest {

    @Autowired
    private CategoryRepositoryAdapter categoryRepositoryAdapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void getAllCategories_returnsAllPersistedCategories() {
        entityManager.persist(new CategoryEntity("Electronics"));
        entityManager.persist(new CategoryEntity("Clothing"));
        entityManager.flush();

        List<Category> result = categoryRepositoryAdapter.getAllCategories();

        assertThat(result)
                .hasSize(2)
                .extracting(Category::name)
                .containsExactlyInAnyOrder("Electronics", "Clothing");
    }

    @Test
    void getAllCategories_returnsEmptyList_whenNoneExist() {
        List<Category> result = categoryRepositoryAdapter.getAllCategories();

        assertThat(result).isEmpty();
    }

    @Test
    void createCategory_persistsAndReturnsCategoryWithGeneratedId() {
        Category result = categoryRepositoryAdapter.createCategory("Electronics");

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Electronics");

        CategoryEntity persisted = entityManager.find(CategoryEntity.class, result.id());
        assertThat(persisted.getName()).isEqualTo("Electronics");
    }

    @Test
    void updateCategory_updatesNameInDatabase() {
        CategoryEntity existing = entityManager.persistFlushFind(new CategoryEntity("Old Name"));

        categoryRepositoryAdapter.updateCategory(new Category(existing.getId(), "New Name"));
        entityManager.flush();
        entityManager.clear();

        CategoryEntity updated = entityManager.find(CategoryEntity.class, existing.getId());
        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    void updateCategory_throwsResourceNotFoundException_whenCategoryDoesNotExist() {
        Category nonExistent = new Category(999L, "Doesn't matter");

        assertThatThrownBy(() -> categoryRepositoryAdapter.updateCategory(nonExistent))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
