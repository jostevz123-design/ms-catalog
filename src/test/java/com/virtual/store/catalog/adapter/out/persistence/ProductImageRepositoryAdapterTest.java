package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.CategoryEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductImageEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductVariantEntity;
import com.virtual.store.catalog.domain.exception.ResourceNotFoundException;
import com.virtual.store.catalog.domain.model.ProductImage;
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
@Import(ProductImageRepositoryAdapter.class)
class ProductImageRepositoryAdapterTest {

    @Autowired
    private ProductImageRepositoryAdapter productImageRepositoryAdapter;

    @Autowired
    private TestEntityManager entityManager;

    private ProductEntity persistProduct() {
        CategoryEntity category = entityManager.persistFlushFind(new CategoryEntity("Clothing"));
        return entityManager.persistFlushFind(new ProductEntity("T-Shirt", "Cotton shirt", "Nike", true, category));
    }

    private ProductVariantEntity persistVariant(ProductEntity product) {
        return entityManager.persistFlushFind(
                new ProductVariantEntity("SKU-BLUE-M", Map.of("color", "blue"), 5000L, 10L, product));
    }

    @Test
    void createProductImage_persistsGeneralProductImage_withoutVariant() {
        ProductEntity product = persistProduct();

        ProductImage result = productImageRepositoryAdapter.createProductImage(
                new ProductImage(null, product.getId(), null, "http://img/general.jpg", true));

        assertThat(result.id()).isNotNull();
        assertThat(result.productId()).isEqualTo(product.getId());
        assertThat(result.productVariantId()).isNull();
        assertThat(result.url()).isEqualTo("http://img/general.jpg");
        assertThat(result.isPrimary()).isTrue();
    }

    @Test
    void createProductImage_persistsVariantSpecificImage() {
        ProductEntity product = persistProduct();
        ProductVariantEntity variant = persistVariant(product);

        ProductImage result = productImageRepositoryAdapter.createProductImage(
                new ProductImage(null, product.getId(), variant.getId(), "http://img/blue.jpg", false));

        assertThat(result.productVariantId()).isEqualTo(variant.getId());
    }

    @Test
    void createProductImage_throwsResourceNotFoundException_whenProductDoesNotExist() {
        ProductImage image = new ProductImage(null, 999L, null, "http://img/general.jpg", true);

        assertThatThrownBy(() -> productImageRepositoryAdapter.createProductImage(image))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void createProductImage_throwsResourceNotFoundException_whenVariantDoesNotExist() {
        ProductEntity product = persistProduct();
        ProductImage image = new ProductImage(null, product.getId(), 999L, "http://img/general.jpg", true);

        assertThatThrownBy(() -> productImageRepositoryAdapter.createProductImage(image))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getProductImageByProductId_returnsAllImagesForThatProduct() {
        ProductEntity product = persistProduct();
        entityManager.persist(new ProductImageEntity("http://img/1.jpg", true, product, null));
        entityManager.persist(new ProductImageEntity("http://img/2.jpg", false, product, null));
        entityManager.flush();

        List<ProductImage> result = productImageRepositoryAdapter.getProductImageByProductId(product.getId());

        assertThat(result)
                .hasSize(2)
                .extracting(ProductImage::url)
                .containsExactlyInAnyOrder("http://img/1.jpg", "http://img/2.jpg");
    }

    @Test
    void getProductImageByProductId_throwsResourceNotFoundException_whenProductDoesNotExist() {
        assertThatThrownBy(() -> productImageRepositoryAdapter.getProductImageByProductId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getProductImageByProductVariantId_returnsOnlyImagesForThatVariant() {
        ProductEntity product = persistProduct();
        ProductVariantEntity variant = persistVariant(product);
        entityManager.persist(new ProductImageEntity("http://img/general.jpg", true, product, null));
        entityManager.persist(new ProductImageEntity("http://img/blue.jpg", false, product, variant));
        entityManager.flush();

        List<ProductImage> result = productImageRepositoryAdapter.getProductImageByProductVariantId(variant.getId());

        assertThat(result)
                .hasSize(1)
                .extracting(ProductImage::url)
                .containsExactly("http://img/blue.jpg");
    }

    @Test
    void getProductImageByProductVariantId_throwsResourceNotFoundException_whenVariantDoesNotExist() {
        assertThatThrownBy(() -> productImageRepositoryAdapter.getProductImageByProductVariantId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void deleteProductImage_removesImage() {
        ProductEntity product = persistProduct();
        ProductImageEntity existing = entityManager.persistFlushFind(
                new ProductImageEntity("http://img/1.jpg", true, product, null));

        productImageRepositoryAdapter.deleteProductImage(existing.getId());
        entityManager.flush();

        assertThat(entityManager.find(ProductImageEntity.class, existing.getId())).isNull();
    }

    @Test
    void deleteProductImage_throwsResourceNotFoundException_whenImageDoesNotExist() {
        assertThatThrownBy(() -> productImageRepositoryAdapter.deleteProductImage(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateProductImage_updatesUrlAndPrimaryFlag() {
        ProductEntity product = persistProduct();
        ProductImageEntity existing = entityManager.persistFlushFind(
                new ProductImageEntity("http://img/old.jpg", false, product, null));

        productImageRepositoryAdapter.updateProductImage(
                new ProductImage(existing.getId(), product.getId(), null, "http://img/new.jpg", true));
        entityManager.flush();
        entityManager.clear();

        ProductImageEntity updated = entityManager.find(ProductImageEntity.class, existing.getId());
        assertThat(updated.getUrlImage()).isEqualTo("http://img/new.jpg");
        assertThat(updated.isPrimary()).isTrue();
    }

    @Test
    void updateProductImage_throwsResourceNotFoundException_whenImageDoesNotExist() {
        ProductImage nonExistent = new ProductImage(999L, 1L, null, "http://img/new.jpg", true);

        assertThatThrownBy(() -> productImageRepositoryAdapter.updateProductImage(nonExistent))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
