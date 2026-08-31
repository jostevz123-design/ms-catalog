package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.CategoryEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.domain.exception.ResourceNotFoundException;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(ProductRepositoryAdapter.class)
class ProductRepositoryAdapterTest {

    @Autowired
    private ProductRepositoryAdapter productRepositoryAdapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void createProduct_persistsAndReturnsProductWithGeneratedId() {
        CategoryEntity category = entityManager.persistFlushFind(new CategoryEntity("Clothing"));

        Product result = productRepositoryAdapter.createProduct(
                new Product(null, "T-Shirt", "Cotton shirt", "Nike", true, category.getId()));

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("T-Shirt");
        assertThat(result.idCategory()).isEqualTo(category.getId());
    }

    @Test
    void createProduct_throwsResourceNotFoundException_whenCategoryDoesNotExist() {
        Product product = new Product(null, "T-Shirt", "Cotton shirt", "Nike", true, 999L);

        assertThatThrownBy(() -> productRepositoryAdapter.createProduct(product))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getAllProducts_returnsPagedResultAcrossCategories() {
        CategoryEntity clothing = entityManager.persistFlushFind(new CategoryEntity("Clothing"));
        CategoryEntity electronics = entityManager.persistFlushFind(new CategoryEntity("Electronics"));
        entityManager.persist(new ProductEntity("T-Shirt", "Cotton shirt", "Nike", true, clothing));
        entityManager.persist(new ProductEntity("Headphones", "Wireless", "Sony", true, electronics));
        entityManager.flush();

        PagedResult<Product> result = productRepositoryAdapter.getAllProducts(0, 20);

        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.page()).isZero();
    }

    @Test
    void getProductsByCategoryId_returnsOnlyProductsInThatCategory() {
        CategoryEntity clothing = entityManager.persistFlushFind(new CategoryEntity("Clothing"));
        CategoryEntity electronics = entityManager.persistFlushFind(new CategoryEntity("Electronics"));
        entityManager.persist(new ProductEntity("T-Shirt", "Cotton shirt", "Nike", true, clothing));
        entityManager.persist(new ProductEntity("Headphones", "Wireless", "Sony", true, electronics));
        entityManager.flush();

        PagedResult<Product> result = productRepositoryAdapter.getProductsByCategoryId(clothing.getId(), 0, 20);

        assertThat(result.content())
                .hasSize(1)
                .extracting(Product::name)
                .containsExactly("T-Shirt");
    }

    @Test
    void getProductsByCategoryId_throwsResourceNotFoundException_whenCategoryDoesNotExist() {
        assertThatThrownBy(() -> productRepositoryAdapter.getProductsByCategoryId(999L, 0, 20))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateProduct_updatesNameDescriptionAndBrand() {
        CategoryEntity category = entityManager.persistFlushFind(new CategoryEntity("Clothing"));
        ProductEntity existing = entityManager.persistFlushFind(
                new ProductEntity("T-Shirt", "Cotton shirt", "Nike", true, category));

        productRepositoryAdapter.updateProduct(
                new Product(existing.getId(), "New Name", "New Description", "New Brand"));
        entityManager.flush();
        entityManager.clear();

        ProductEntity updated = entityManager.find(ProductEntity.class, existing.getId());
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getDescription()).isEqualTo("New Description");
        assertThat(updated.getBrand()).isEqualTo("New Brand");
    }

    @Test
    void updateProduct_throwsResourceNotFoundException_whenProductDoesNotExist() {
        Product nonExistent = new Product(999L, "Name", "Description", "Brand");

        assertThatThrownBy(() -> productRepositoryAdapter.updateProduct(nonExistent))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void changeActiveStatus_updatesActiveFlag() {
        CategoryEntity category = entityManager.persistFlushFind(new CategoryEntity("Clothing"));
        ProductEntity existing = entityManager.persistFlushFind(
                new ProductEntity("T-Shirt", "Cotton shirt", "Nike", true, category));

        productRepositoryAdapter.changeActiveStatus(existing.getId(), false);
        entityManager.flush();
        entityManager.clear();

        ProductEntity updated = entityManager.find(ProductEntity.class, existing.getId());
        assertThat(updated.isActive()).isFalse();
    }

    @Test
    void changeActiveStatus_throwsResourceNotFoundException_whenProductDoesNotExist() {
        assertThatThrownBy(() -> productRepositoryAdapter.changeActiveStatus(999L, false))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
