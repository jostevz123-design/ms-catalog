package com.virtual.store.catalog.adapter.in.web;

import com.virtual.store.catalog.application.port.in.ProductUseCase;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;
import com.virtual.store.catalog.domain.model.ProductCatalogItem;
import com.virtual.store.catalog.domain.model.ProductDetail;
import com.virtual.store.catalog.domain.model.ProductImage;
import com.virtual.store.catalog.domain.model.ProductVariant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductUseCase productUseCase;

    @Test
    void getAllProducts_returns200WithPagedProducts() throws Exception {
        List<ProductCatalogItem> products = List.of(
                new ProductCatalogItem(1L, "T-Shirt", "Nike", 5000L, "http://img/t-shirt.jpg"),
                new ProductCatalogItem(2L, "Sneakers", "Adidas", 8000L, "http://img/sneakers.jpg")
        );
        when(productUseCase.getAllProducts(0)).thenReturn(new PagedResult<>(products, 0, 20, 2, 1));

        mockMvc.perform(get("/api/v1/products/page/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("T-Shirt"))
                .andExpect(jsonPath("$.content[0].brand").value("Nike"))
                .andExpect(jsonPath("$.content[0].price").value(5000))
                .andExpect(jsonPath("$.content[0].imageUrl").value("http://img/t-shirt.jpg"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getProductsByCategory_returns200WithPagedProducts() throws Exception {
        List<ProductCatalogItem> products = List.of(
                new ProductCatalogItem(1L, "T-Shirt", "Nike", 3000L, "http://img/t-shirt.jpg")
        );
        when(productUseCase.getProductsByCategoryId(5L, 0)).thenReturn(new PagedResult<>(products, 0, 20, 1, 1));

        mockMvc.perform(get("/api/v1/products/category/5/page/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("T-Shirt"))
                .andExpect(jsonPath("$.content[0].price").value(3000));
    }

    @Test
    void getProductDetail_returns200WithVariantsAndImages() throws Exception {
        Product product = new Product(1L, "T-Shirt", "Cotton shirt", "Nike", true, 5L);
        ProductVariant variant = new ProductVariant(10L, 1L, "SKU-BLUE", java.util.Map.of("color", "blue"), 5000L, 10L);
        List<ProductImage> images = List.of(
                new ProductImage(100L, 1L, 10L, "http://img/blue.jpg", true),
                new ProductImage(101L, 1L, null, "http://img/general.jpg", true)
        );
        ProductDetail detail = new ProductDetail(product, List.of(variant), images);
        when(productUseCase.getProductDetailByProductId(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/products/detail/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("T-Shirt"))
                .andExpect(jsonPath("$.generalImagersUrls[0]").value("http://img/general.jpg"))
                .andExpect(jsonPath("$.productVariants.length()").value(1))
                .andExpect(jsonPath("$.productVariants[0].sku").value("SKU-BLUE"))
                .andExpect(jsonPath("$.productVariants[0].imageUrls[0]").value("http://img/blue.jpg"));
    }
}
