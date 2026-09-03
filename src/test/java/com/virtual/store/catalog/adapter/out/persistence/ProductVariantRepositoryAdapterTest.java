package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.CategoryEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductVariantEntity;
import com.virtual.store.catalog.domain.exception.ResourceNotFoundException;
import com.virtual.store.catalog.domain.model.ProductVariant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(ProductVariantRepositoryAdapter.class)
class ProductVariantRepositoryAdapterTest {

    @Autowired
    private ProductVariantRepositoryAdapter productVariantRepositoryAdapter;

    @Autowired
    private TestEntityManager entityManager;

    private ProductEntity persistProduct() {
        CategoryEntity category = entityManager.persistFlushFind(new CategoryEntity("Clothing"));
        return entityManager.persistFlushFind(new ProductEntity("T-Shirt", "Cotton shirt", "Nike", true, category));
    }

    @Test
    void createProductVariant_persistsAndReturnsVariantWithGeneratedId() {
        ProductEntity product = persistProduct();

        ProductVariant result = productVariantRepositoryAdapter.createProductVariant(
                new ProductVariant(null, product.getId(), "SKU-BLUE-M", Map.of("color", "blue", "talla", "M"), 5000L, 10L));

        assertThat(result.id()).isNotNull();
        assertThat(result.sku()).isEqualTo("SKU-BLUE-M");
        assertThat(result.productId()).isEqualTo(product.getId());
        assertThat(result.price()).isEqualTo(5000L);
        assertThat(result.stock()).isEqualTo(10L);
    }

    @Test
    void createProductVariant_throwsResourceNotFoundException_whenProductDoesNotExist() {
        ProductVariant variant = new ProductVariant(null, 999L, "SKU-1", Map.of(), 5000L, 10L);

        assertThatThrownBy(() -> productVariantRepositoryAdapter.createProductVariant(variant))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateProductVariant_updatesPriceAttributesAndStock_butNotSku() {
        ProductEntity product = persistProduct();
        ProductVariantEntity existing = entityManager.persistFlushFind(
                new ProductVariantEntity("SKU-BLUE-M", Map.of("color", "blue"), 5000L, 10L, product));

        productVariantRepositoryAdapter.updateProductVariant(
                new ProductVariant(existing.getId(), product.getId(), "SKU-IGNORED", Map.of("color", "red"), 4500L, 3L));
        entityManager.flush();
        entityManager.clear();

        ProductVariantEntity updated = entityManager.find(ProductVariantEntity.class, existing.getId());
        assertThat(updated.getPrice()).isEqualTo(4500L);
        assertThat(updated.getStock()).isEqualTo(3L);
        assertThat(updated.getAttributes()).isEqualTo(Map.of("color", "red"));
        assertThat(updated.getSku()).isEqualTo("SKU-BLUE-M");
    }

    @Test
    void updateProductVariant_throwsResourceNotFoundException_whenVariantDoesNotExist() {
        ProductVariant nonExistent = new ProductVariant(999L, 1L, "SKU-1", Map.of(), 5000L, 10L);

        assertThatThrownBy(() -> productVariantRepositoryAdapter.updateProductVariant(nonExistent))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateVariantStock_updatesStockOnly() {
        ProductEntity product = persistProduct();
        ProductVariantEntity existing = entityManager.persistFlushFind(
                new ProductVariantEntity("SKU-BLUE-M", Map.of("color", "blue"), 5000L, 10L, product));

        productVariantRepositoryAdapter.updateVariantStock(existing.getId(), 2L);
        entityManager.flush();
        entityManager.clear();

        ProductVariantEntity updated = entityManager.find(ProductVariantEntity.class, existing.getId());
        assertThat(updated.getStock()).isEqualTo(2L);
        assertThat(updated.getPrice()).isEqualTo(5000L);
    }

    @Test
    void updateVariantStock_throwsResourceNotFoundException_whenVariantDoesNotExist() {
        assertThatThrownBy(() -> productVariantRepositoryAdapter.updateVariantStock(999L, 2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getVariantsByProductId_returnsAllVariantsForThatProduct() {
        ProductEntity product = persistProduct();
        entityManager.persist(new ProductVariantEntity("SKU-BLUE-M", Map.of("color", "blue"), 5000L, 10L, product));
        entityManager.persist(new ProductVariantEntity("SKU-RED-M", Map.of("color", "red"), 4800L, 4L, product));
        entityManager.flush();

        List<ProductVariant> result = productVariantRepositoryAdapter.getVariantsByProductId(product.getId());

        assertThat(result)
                .hasSize(2)
                .extracting(ProductVariant::sku)
                .containsExactlyInAnyOrder("SKU-BLUE-M", "SKU-RED-M");
    }

    @Test
    void getVariantsByProductId_throwsResourceNotFoundException_whenProductDoesNotExist() {
        assertThatThrownBy(() -> productVariantRepositoryAdapter.getVariantsByProductId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
