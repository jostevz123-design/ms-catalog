package com.virtual.store.catalog.adapter.in.web;

import com.virtual.store.catalog.adapter.in.web.dto.CategoryResponse;
import com.virtual.store.catalog.application.port.in.CategoryUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryUseCase categoryUseCase;

    public CategoryController(CategoryUseCase categoryUseCase){
        this.categoryUseCase = categoryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        var categories = categoryUseCase.getAllCategories();
        List<CategoryResponse> categoryResponseList = categories.stream()
                .map(CategoryResponse::from)
                .toList();

        return ResponseEntity.ok(categoryResponseList);
    }
}
