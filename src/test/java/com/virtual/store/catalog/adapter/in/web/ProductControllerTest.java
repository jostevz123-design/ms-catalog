package com.virtual.store.catalog.adapter.in.web;

import com.virtual.store.catalog.application.port.in.ProductUseCase;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;
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
        List<Product> products = List.of(
                new Product(1L, "T-Shirt", "Cotton shirt", "Nike", true, 5L),
                new Product(2L, "Sneakers", "Running shoes", "Adidas", true, 5L)
        );
        when(productUseCase.getAllProducts(0)).thenReturn(new PagedResult<>(products, 0, 20, 2, 1));

        mockMvc.perform(get("/api/v1/products/page/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("T-Shirt"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getProductsByCategory_returns200WithPagedProducts() throws Exception {
        List<Product> products = List.of(
                new Product(1L, "T-Shirt", "Cotton shirt", "Nike", true, 5L)
        );
        when(productUseCase.getProductsByCategoryId(5L, 0)).thenReturn(new PagedResult<>(products, 0, 20, 1, 1));

        mockMvc.perform(get("/api/v1/products/category/5/page/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].categoryId").value(5));
    }
}
