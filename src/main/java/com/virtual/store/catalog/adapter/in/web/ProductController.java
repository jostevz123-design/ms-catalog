package com.virtual.store.catalog.adapter.in.web;

import com.virtual.store.catalog.adapter.in.web.dto.PageResultResponse;
import com.virtual.store.catalog.adapter.in.web.dto.ProductCatalogResponse;
import com.virtual.store.catalog.application.port.in.ProductUseCase;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.ProductCatalogItem;
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
    public ResponseEntity<PageResultResponse<ProductCatalogResponse>> getAllProducts(@PathVariable("pageNumber") int pageNumber){
        PagedResult<ProductCatalogItem> productPagedResult = productUseCase.getAllProducts(pageNumber);
        List<ProductCatalogResponse> productResponseList = productPagedResult.content().stream()
                    .map(ProductCatalogResponse::from)
                .toList();

        PageResultResponse<ProductCatalogResponse> pagedResult = new PageResultResponse(
                productResponseList,
                productPagedResult.page(),
                productPagedResult.size(),
                productPagedResult.totalElements(),
                productPagedResult.totalPages()
        );
        return ResponseEntity.ok(pagedResult);
    }

    @GetMapping("/category/{categoryId}/page/{pageNumber}")
    public ResponseEntity<PageResultResponse<ProductCatalogResponse>> getProductsByCategory(@PathVariable("categoryId") Long categoryId, @PathVariable("pageNumber") int pageNumber){
        PagedResult<ProductCatalogItem> productPagedResult = productUseCase.getProductsByCategoryId(categoryId, pageNumber);
        List<ProductCatalogResponse> productResponseList = productPagedResult.content().stream()
                .map(ProductCatalogResponse::from)
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
