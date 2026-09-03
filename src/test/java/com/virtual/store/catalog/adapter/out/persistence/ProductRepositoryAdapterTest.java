package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.CategoryEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductImageEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductVariantEntity;
import com.virtual.store.catalog.domain.exception.ResourceNotFoundException;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;
import com.virtual.store.catalog.domain.model.ProductCatalogItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Map;

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
    void getAllProducts_returnsMinPriceAcrossVariantsAndPrimaryImage() {
        CategoryEntity category = entityManager.persistFlushFind(new CategoryEntity("Clothing"));
        ProductEntity product = entityManager.persistFlushFind(
                new ProductEntity("T-Shirt", "Cotton shirt", "Nike", true, category));
        entityManager.persist(new ProductVariantEntity("SKU-BLUE", Map.of("color", "blue"), 5000L, 10L, product));
        entityManager.persist(new ProductVariantEntity("SKU-RED", Map.of("color", "red"), 4500L, 5L, product));
        entityManager.persist(new ProductImageEntity("http://img/primary.jpg", true, product, null));
        entityManager.persist(new ProductImageEntity("http://img/secondary.jpg", false, product, null));
        entityManager.flush();

        PagedResult<ProductCatalogItem> result = productRepositoryAdapter.getAllProducts(0, 20);

        assertThat(result.content()).hasSize(1);
        ProductCatalogItem item = result.content().get(0);
        assertThat(item.name()).isEqualTo("T-Shirt");
        assertThat(item.price()).isEqualTo(4500L);
        assertThat(item.imageUrl()).isEqualTo("http://img/primary.jpg");
    }

    @Test
    void getAllProducts_excludesProductsWithoutAnyVariant() {
        CategoryEntity category = entityManager.persistFlushFind(new CategoryEntity("Clothing"));
        entityManager.persistFlushFind(new ProductEntity("No Variant Product", "desc", "Brand", true, category));

        PagedResult<ProductCatalogItem> result = productRepositoryAdapter.getAllProducts(0, 20);

        assertThat(result.content()).isEmpty();
    }

    @Test
    void getAllProducts_returnsNullImageUrl_whenNoPrimaryImageExists() {
        CategoryEntity category = entityManager.persistFlushFind(new CategoryEntity("Clothing"));
        ProductEntity product = entityManager.persistFlushFind(
                new ProductEntity("T-Shirt", "Cotton shirt", "Nike", true, category));
        entityManager.persist(new ProductVariantEntity("SKU-BLUE", Map.of("color", "blue"), 5000L, 10L, product));
        entityManager.flush();

        PagedResult<ProductCatalogItem> result = productRepositoryAdapter.getAllProducts(0, 20);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).imageUrl()).isNull();
    }

    @Test
    void getProductsByCategoryId_returnsOnlyProductsInThatCategoryWithPrice() {
        CategoryEntity clothing = entityManager.persistFlushFind(new CategoryEntity("Clothing"));
        CategoryEntity electronics = entityManager.persistFlushFind(new CategoryEntity("Electronics"));
        ProductEntity shirt = entityManager.persistFlushFind(
                new ProductEntity("T-Shirt", "Cotton shirt", "Nike", true, clothing));
        ProductEntity headphones = entityManager.persistFlushFind(
                new ProductEntity("Headphones", "Wireless", "Sony", true, electronics));
        entityManager.persist(new ProductVariantEntity("SKU-SHIRT", Map.of("color", "blue"), 3000L, 10L, shirt));
        entityManager.persist(new ProductVariantEntity("SKU-HEADPHONES", Map.of("color", "black"), 8000L, 10L, headphones));
        entityManager.flush();

        PagedResult<ProductCatalogItem> result = productRepositoryAdapter.getProductsByCategoryId(clothing.getId(), 0, 20);

        assertThat(result.content())
                .hasSize(1)
                .extracting(ProductCatalogItem::name)
                .containsExactly("T-Shirt");
        assertThat(result.content().get(0).price()).isEqualTo(3000L);
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
