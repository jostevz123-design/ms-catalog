package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.CategoryEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.adapter.out.persistence.mapper.ProductMapper;
import com.virtual.store.catalog.application.port.out.ProductRepository;
import com.virtual.store.catalog.domain.exception.ResourceNotFoundException;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;

    public ProductRepositoryAdapter(ProductJpaRepository productJpaRepository, CategoryJpaRepository categoryJpaRepository){
        this.productJpaRepository = productJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public Product createProduct(Product product) {
        if(!categoryJpaRepository.existsById(product.idCategory())){
            throw new ResourceNotFoundException(" Category with id="+ product.idCategory() + " does not exists");
        }
        CategoryEntity categoryEntity = categoryJpaRepository.getReferenceById(product.idCategory());
        ProductEntity productEntity = ProductMapper.toEntity(product, categoryEntity);
        productJpaRepository.save(productEntity);
        return ProductMapper.toDomain(productEntity);
    }

    @Override
    public PagedResult<Product> getAllProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ProductEntity> pageProducts =  productJpaRepository.findAll(pageable);
        return pageResultDomain(pageProducts);
    }

    @Override
    public PagedResult<Product> getProductsByCategoryId(Long categoryId, int pageNumber, int pageSize) {
        if(!categoryJpaRepository.existsById(categoryId)){
            throw new ResourceNotFoundException(" Category with id="+ categoryId + " does not exists");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ProductEntity> pageProducts = productJpaRepository.findByCategoryEntityId(categoryId, pageable);
        return pageResultDomain(pageProducts);
    }

    @Override
    public void updateProduct(Product product) {
        ProductEntity productEntity = productJpaRepository.findById(product.id())
                .orElseThrow(()-> new ResourceNotFoundException(" Product with id=" + product.id() + " doesn not exist"));

        productEntity.setName(product.name());
        productEntity.setDescription(product.description());
        productEntity.setBrand(product.brand());
    }

    @Override
    public void changeActiveStatus(Long productId, boolean active) {
        ProductEntity productEntity = productJpaRepository.findById(productId).
                orElseThrow(()-> new ResourceNotFoundException(" Product with id=" + productId + " doesn not exist"));
        productEntity.setActive(active);
    }

    private PagedResult<Product> pageResultDomain(Page<ProductEntity> pageProducts){
        List<Product> productList = pageProducts.getContent().stream()
                .map(ProductMapper::toDomain)
                .toList();
        return new PagedResult<>(
                productList,
                pageProducts.getNumber(),
                pageProducts.getSize(),
                pageProducts.getTotalElements(),
                pageProducts.getTotalPages()
        );
    }
}
