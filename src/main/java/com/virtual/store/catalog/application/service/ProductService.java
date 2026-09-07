package com.virtual.store.catalog.application.service;

import com.virtual.store.catalog.application.port.in.ProductUseCase;
import com.virtual.store.catalog.application.port.out.ProductImageRepository;
import com.virtual.store.catalog.application.port.out.ProductRepository;
import com.virtual.store.catalog.application.port.out.ProductVariantRepository;
import com.virtual.store.catalog.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService implements ProductUseCase {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private static final int PAGE_SIZE=20;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;


    public ProductService(ProductRepository productRepository, ProductVariantRepository productVariantRepository, ProductImageRepository productImageRepository){
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.productImageRepository = productImageRepository;
    }


    @Override
    @Transactional
    public Product createProduct(String name, String description, String brand, Long categoryId) {
        Product product = new Product(null, name, description, brand, true, categoryId);
        Product productCreated = productRepository.createProduct(product);
        log.info("Product created with id={}", productCreated.id());
        return productCreated;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<ProductCatalogItem> getAllProducts(int pageNumber) {
        PagedResult<ProductCatalogItem> pagedResult = productRepository.getAllProducts(pageNumber,PAGE_SIZE);
        return generateCatalogImageUrl(pagedResult);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<ProductCatalogItem> getProductsByCategoryId(Long categoryId, int pageNumber) {
        PagedResult<ProductCatalogItem> pagedResult = productRepository.getProductsByCategoryId(categoryId, pageNumber, PAGE_SIZE);
        return generateCatalogImageUrl(pagedResult);
    }

    @Override
    @Transactional
    public void updateProduct(Long id, String name, String description, String brand) {
        Product product = new Product(id, name, description, brand);
        productRepository.updateProduct(product);
        log.info(" Product with id={}, was updated successfully", id);
    }

    @Override
    @Transactional
    public void changeActiveStatus(Long productId, boolean active) {
        productRepository.changeActiveStatus(productId, active);
        log.info(" The product with id={} was changed to active={}", productId, active);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetail getProductDetailByProductId(Long productId) {
        Product product = productRepository.getProductById(productId);
        List<ProductVariant> productVariantList = productVariantRepository.getVariantsByProductId(productId);
        List<ProductImage> productImageList = productImageRepository.getProductImageByProductId(productId);

        return new ProductDetail(product, productVariantList, productImageList);
    }


    private PagedResult<ProductCatalogItem> generateCatalogImageUrl(PagedResult<ProductCatalogItem> pagedResult){
        List<Long> productIds = pagedResult.content()
                .stream()
                .map(ProductCatalogItem::id)
                .toList();

        List<ProductImage> productImageList = productImageRepository.getPrimaryImageUrlsByProductIds(productIds);

        Map<Long, String> productImageUrlMap = productImageList.stream()
                .collect(Collectors.toMap(
                        item -> item.productId(),
                        ProductImage::url
                ));


        List<ProductCatalogItem> resultCatalog = pagedResult.content().stream()
                .map( productItem ->
                        new ProductCatalogItem(
                                productItem.id(),
                                productItem.name(),
                                productItem.brand(),
                                productItem.price(),
                                productImageUrlMap.getOrDefault(productItem.id(), "")
                        )
                )
                .toList();

        return new PagedResult<>(
                resultCatalog,
                pagedResult.page(),
                pagedResult.size(),
                pagedResult.totalElements(),
                pagedResult.totalPages()
        );
    }
}
