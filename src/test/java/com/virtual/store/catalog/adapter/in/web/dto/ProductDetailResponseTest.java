package com.virtual.store.catalog.adapter.in.web.dto;

import com.virtual.store.catalog.domain.model.Product;
import com.virtual.store.catalog.domain.model.ProductDetail;
import com.virtual.store.catalog.domain.model.ProductImage;
import com.virtual.store.catalog.domain.model.ProductVariant;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDetailResponseTest {

    private static final Product PRODUCT = new Product(1L, "T-Shirt", "Cotton shirt", "Nike", true, 5L);

    @Test
    void from_mapsProductFieldsDirectly() {
        ProductDetail detail = new ProductDetail(PRODUCT, List.of(), List.of());

        ProductDetailResponse response = ProductDetailResponse.from(detail);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("T-Shirt");
        assertThat(response.description()).isEqualTo("Cotton shirt");
        assertThat(response.brand()).isEqualTo("Nike");
        assertThat(response.isActive()).isTrue();
    }

    @Test
    void from_separatesGeneralImagesFromVariantSpecificImages() {
        ProductVariant blue = new ProductVariant(10L, 1L, "SKU-BLUE", Map.of("color", "blue"), 5000L, 10L);
        ProductVariant red = new ProductVariant(11L, 1L, "SKU-RED", Map.of("color", "red"), 4800L, 3L);

        List<ProductImage> images = List.of(
                new ProductImage(100L, 1L, 10L, "http://img/blue.jpg", true),
                new ProductImage(101L, 1L, null, "http://img/general.jpg", true),
                new ProductImage(102L, 1L, 11L, "http://img/red-1.jpg", false),
                new ProductImage(103L, 1L, 11L, "http://img/red-2.jpg", false)
        );

        ProductDetail detail = new ProductDetail(PRODUCT, List.of(blue, red), images);

        ProductDetailResponse response = ProductDetailResponse.from(detail);

        assertThat(response.generalImageUrls()).containsExactly("http://img/general.jpg");

        ProductVariantResponse blueResponse = response.productVariants().stream()
                .filter(v -> v.id().equals(10L))
                .findFirst()
                .orElseThrow();
        assertThat(blueResponse.imageUrls()).containsExactly("http://img/blue.jpg");

        ProductVariantResponse redResponse = response.productVariants().stream()
                .filter(v -> v.id().equals(11L))
                .findFirst()
                .orElseThrow();
        assertThat(redResponse.imageUrls()).containsExactlyInAnyOrder("http://img/red-1.jpg", "http://img/red-2.jpg");
    }

    @Test
    void from_returnsEmptyImageList_whenVariantHasNoImages() {
        ProductVariant variantWithoutImages = new ProductVariant(10L, 1L, "SKU-BLUE", Map.of("color", "blue"), 5000L, 10L);
        ProductDetail detail = new ProductDetail(PRODUCT, List.of(variantWithoutImages), List.of());

        ProductDetailResponse response = ProductDetailResponse.from(detail);

        assertThat(response.productVariants()).hasSize(1);
        assertThat(response.productVariants().get(0).imageUrls()).isNotNull().isEmpty();
    }

    @Test
    void from_returnsEmptyGeneralImages_whenNoImagesExist() {
        ProductDetail detail = new ProductDetail(PRODUCT, List.of(), List.of());

        ProductDetailResponse response = ProductDetailResponse.from(detail);

        assertThat(response.generalImageUrls()).isEmpty();
        assertThat(response.productVariants()).isEmpty();
    }
}
