package com.virtual.store.catalog.adapter.in.web.dto;

import com.virtual.store.catalog.domain.model.ProductDetail;
import com.virtual.store.catalog.domain.model.ProductImage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ProductDetailResponse(
        Long id,
        String name,
        String description,
        String brand,
        boolean isActive,
        List<ProductVariantResponse> productVariants,
        List<String> generalImageUrls
) {

    public static ProductDetailResponse from(ProductDetail productDetail){

        Map<Long, List<String>> imagesByVariantId = productDetail.productImageList().stream()
                .filter( image -> image.productVariantId() != null)
                .collect(
                        Collectors.groupingBy(ProductImage ::productVariantId,
                                Collectors.mapping(ProductImage::url, Collectors.toList()))
                );

        List<String> generalImages = productDetail.productImageList().stream()
                .filter( image -> image.productVariantId() == null)
                .map(ProductImage::url)
                .toList();


        List<ProductVariantResponse> productVariantResponseList = productDetail.productVariantList().stream()
                .map(variant -> ProductVariantResponse.from(variant, imagesByVariantId.getOrDefault(variant.id(), List.of())))
                .toList();


        return new ProductDetailResponse(
                productDetail.product().id(),
                productDetail.product().name(),
                productDetail.product().description(),
                productDetail.product().brand(),
                productDetail.product().isActive(),
                productVariantResponseList,
                generalImages
        );
    }
}
