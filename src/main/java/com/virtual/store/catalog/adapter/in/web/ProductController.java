package com.virtual.store.catalog.adapter.in.web;

import com.virtual.store.catalog.adapter.in.web.dto.PageResultResponse;
import com.virtual.store.catalog.adapter.in.web.dto.ProductResponse;
import com.virtual.store.catalog.application.port.in.ProductUseCase;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase){
        this.productUseCase = productUseCase;
    }


    @GetMapping("/page/{pageNumber}")
    public ResponseEntity<PageResultResponse<ProductResponse>> getAllProducts(@PathVariable("pageNumber") int pageNumber){
        PagedResult<Product> productPagedResult = productUseCase.getAllProducts(pageNumber);
        List<ProductResponse> productResponseList = productPagedResult.content().stream()
                    .map(ProductResponse::from)
                .toList();

        PageResultResponse<ProductResponse> pagedResult = new PageResultResponse(
                productResponseList,
                productPagedResult.page(),
                productPagedResult.size(),
                productPagedResult.totalElements(),
                productPagedResult.totalPages()
        );
        return ResponseEntity.ok(pagedResult);
    }

    @GetMapping("/category/{categoryId}/page/{pageNumber}")
    public ResponseEntity<PageResultResponse<ProductResponse>> getProductsByCategory(@PathVariable("categoryId") Long categoryId, @PathVariable("pageNumber") int pageNumber){
        PagedResult<Product> productPagedResult = productUseCase.getProductsByCategoryId(categoryId, pageNumber);
        List<ProductResponse> productResponseList = productPagedResult.content().stream()
                .map(ProductResponse::from)
                .toList();

        PageResultResponse pagedResult = new PageResultResponse(
                productResponseList,
                productPagedResult.page(),
                productPagedResult.size(),
                productPagedResult.totalElements(),
                productPagedResult.totalPages()
        );
        return ResponseEntity.ok(pagedResult);
    }
}
