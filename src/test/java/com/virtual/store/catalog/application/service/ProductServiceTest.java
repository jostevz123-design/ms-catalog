package com.virtual.store.catalog.application.service;

import com.virtual.store.catalog.application.port.out.ProductImageRepository;
import com.virtual.store.catalog.application.port.out.ProductRepository;
import com.virtual.store.catalog.application.port.out.ProductVariantRepository;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;
import com.virtual.store.catalog.domain.model.ProductCatalogItem;
import com.virtual.store.catalog.domain.model.ProductDetail;
import com.virtual.store.catalog.domain.model.ProductImage;
import com.virtual.store.catalog.domain.model.ProductVariant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final int PAGE_SIZE = 20;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_delegatesToRepositoryAndReturnsCreatedProduct() {
        Product created = new Product(1L, "T-Shirt", "Cotton shirt", "Nike", true, 5L);
        when(productRepository.createProduct(any())).thenReturn(created);

        Product result = productService.createProduct("T-Shirt", "Cotton shirt", "Nike", 5L);

        assertThat(result).isEqualTo(created);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).createProduct(captor.capture());
        Product sentToRepository = captor.getValue();
        assertThat(sentToRepository.id()).isNull();
        assertThat(sentToRepository.name()).isEqualTo("T-Shirt");
        assertThat(sentToRepository.isActive()).isTrue();
        assertThat(sentToRepository.idCategory()).isEqualTo(5L);
    }

    @Test
    void getAllProducts_delegatesToRepositoryWithDefaultPageSize() {
        PagedResult<ProductCatalogItem> page = new PagedResult<>(List.of(), 0, PAGE_SIZE, 0, 0);
        when(productRepository.getAllProducts(0, PAGE_SIZE)).thenReturn(page);
        when(productImageRepository.getPrimaryImageUrlsByProductIds(List.of())).thenReturn(List.of());

        PagedResult<ProductCatalogItem> result = productService.getAllProducts(0);

        assertThat(result.content()).isEmpty();
        verify(productRepository).getAllProducts(0, PAGE_SIZE);
    }

    @Test
    void getAllProducts_mergesPrimaryImageUrlIntoEachCatalogItem() {
        ProductCatalogItem withImage = new ProductCatalogItem(1L, "T-Shirt", "Nike", 5000L, null);
        ProductCatalogItem withoutImage = new ProductCatalogItem(2L, "Sneakers", "Adidas", 8000L, null);
        PagedResult<ProductCatalogItem> page = new PagedResult<>(List.of(withImage, withoutImage), 0, PAGE_SIZE, 2, 1);
        when(productRepository.getAllProducts(0, PAGE_SIZE)).thenReturn(page);
        when(productImageRepository.getPrimaryImageUrlsByProductIds(List.of(1L, 2L)))
                .thenReturn(List.of(new ProductImage(10L, 1L, null, "http://img/t-shirt.jpg", true)));

        PagedResult<ProductCatalogItem> result = productService.getAllProducts(0);

        assertThat(result.content())
                .extracting(ProductCatalogItem::id, ProductCatalogItem::imageUrl)
                .containsExactly(
                        tuple(1L, "http://img/t-shirt.jpg"),
                        tuple(2L, "")
                );
    }

    @Test
    void getProductsByCategoryId_delegatesToRepositoryWithDefaultPageSize() {
        PagedResult<ProductCatalogItem> page = new PagedResult<>(List.of(), 0, PAGE_SIZE, 0, 0);
        when(productRepository.getProductsByCategoryId(5L, 0, PAGE_SIZE)).thenReturn(page);
        when(productImageRepository.getPrimaryImageUrlsByProductIds(List.of())).thenReturn(List.of());

        PagedResult<ProductCatalogItem> result = productService.getProductsByCategoryId(5L, 0);

        assertThat(result.content()).isEmpty();
        verify(productRepository).getProductsByCategoryId(5L, 0, PAGE_SIZE);
    }

    @Test
    void updateProduct_delegatesToRepositoryWithUpdatedFields() {
        productService.updateProduct(1L, "New Name", "New Description", "New Brand");

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).updateProduct(captor.capture());
        Product sentToRepository = captor.getValue();
        assertThat(sentToRepository.id()).isEqualTo(1L);
        assertThat(sentToRepository.name()).isEqualTo("New Name");
        assertThat(sentToRepository.description()).isEqualTo("New Description");
        assertThat(sentToRepository.brand()).isEqualTo("New Brand");
    }

    @Test
    void changeActiveStatus_delegatesToRepository() {
        productService.changeActiveStatus(1L, false);

        verify(productRepository).changeActiveStatus(1L, false);
    }

    @Test
    void getProductDetailByProductId_assemblesProductVariantsAndImages() {
        Product product = new Product(1L, "T-Shirt", "Cotton shirt", "Nike", true, 5L);
        List<ProductVariant> variants = List.of(
                new ProductVariant(10L, 1L, "SKU-BLUE", java.util.Map.of("color", "blue"), 5000L, 10L));
        List<ProductImage> images = List.of(
                new ProductImage(100L, 1L, 10L, "http://img/blue.jpg", true));

        when(productRepository.getProductById(1L)).thenReturn(product);
        when(productVariantRepository.getVariantsByProductId(1L)).thenReturn(variants);
        when(productImageRepository.getProductImageByProductId(1L)).thenReturn(images);

        ProductDetail result = productService.getProductDetailByProductId(1L);

        assertThat(result.product()).isEqualTo(product);
        assertThat(result.productVariantList()).isEqualTo(variants);
        assertThat(result.productImageList()).isEqualTo(images);
    }
}
