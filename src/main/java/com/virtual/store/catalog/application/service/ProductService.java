package com.virtual.store.catalog.application.service;

import com.virtual.store.catalog.application.port.in.ProductUseCase;
import com.virtual.store.catalog.application.port.out.ProductRepository;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService implements ProductUseCase {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final static int PAGE_SIZE=20;
    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
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
    public PagedResult<Product> getAllProducts(int pageNumber) {
        return productRepository.getAllProducts(pageNumber,PAGE_SIZE);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Product> getProductsByCategoryId(Long categoryId, int pageNumber) {
        return productRepository.getProductsByCategoryId(categoryId, pageNumber, PAGE_SIZE);
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
}
