package com.virtual.store.catalog.application.service;

import com.virtual.store.catalog.application.port.out.ProductRepository;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final int PAGE_SIZE = 20;

    @Mock
    private ProductRepository productRepository;

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
        PagedResult<Product> page = new PagedResult<>(List.of(), 0, PAGE_SIZE, 0, 0);
        when(productRepository.getAllProducts(0, PAGE_SIZE)).thenReturn(page);

        PagedResult<Product> result = productService.getAllProducts(0);

        assertThat(result).isEqualTo(page);
        verify(productRepository).getAllProducts(0, PAGE_SIZE);
    }

    @Test
    void getProductsByCategoryId_delegatesToRepositoryWithDefaultPageSize() {
        PagedResult<Product> page = new PagedResult<>(List.of(), 0, PAGE_SIZE, 0, 0);
        when(productRepository.getProductsByCategoryId(5L, 0, PAGE_SIZE)).thenReturn(page);

        PagedResult<Product> result = productService.getProductsByCategoryId(5L, 0);

        assertThat(result).isEqualTo(page);
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
}
