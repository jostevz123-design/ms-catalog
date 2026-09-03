package com.virtual.store.catalog.adapter.in.web;

import com.virtual.store.catalog.application.port.in.ProductUseCase;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.ProductCatalogItem;
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
}
