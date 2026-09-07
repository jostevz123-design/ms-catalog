package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.CategoryEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.adapter.out.persistence.mapper.ProductCatalogMapper;
import com.virtual.store.catalog.adapter.out.persistence.mapper.ProductMapper;
import com.virtual.store.catalog.adapter.out.persistence.projection.ProductCatalogProjection;
import com.virtual.store.catalog.application.port.out.ProductRepository;
import com.virtual.store.catalog.application.port.out.ProductVariantRepository;
import com.virtual.store.catalog.domain.exception.ResourceNotFoundException;
import com.virtual.store.catalog.domain.model.PagedResult;
import com.virtual.store.catalog.domain.model.Product;
import com.virtual.store.catalog.domain.model.ProductCatalogItem;
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
    public PagedResult<ProductCatalogItem> getAllProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ProductCatalogProjection> pageProducts =  productJpaRepository.findCatalog(pageable);
        return toPagedProductCatalogItem(pageProducts);
    }

    @Override
    public PagedResult<ProductCatalogItem> getProductsByCategoryId(Long categoryId, int pageNumber, int pageSize) {
        if(!categoryJpaRepository.existsById(categoryId)){
            throw new ResourceNotFoundException(" Category with id="+ categoryId + " does not exists");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ProductCatalogProjection> pageProducts = productJpaRepository.findByCategoryEntityId(categoryId, pageable);
        return toPagedProductCatalogItem(pageProducts);
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

    @Override
    public Product getProductById(Long productId) {
        ProductEntity productEntity = productJpaRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException(" Product with id=" + productId + " doesn not exist"));


        return ProductMapper.toDomain(productEntity);
    }

    private PagedResult<ProductCatalogItem> toPagedProductCatalogItem(Page<ProductCatalogProjection> productCatalogPage){


        List<ProductCatalogItem> productCatalogItemList = productCatalogPage.getContent().stream()
                .map( ProductCatalogMapper::toDomain)
                .toList();


        return new PagedResult<>(
                productCatalogItemList,
                productCatalogPage.getNumber(),
                productCatalogPage.getSize(),
                productCatalogPage.getTotalElements(),
                productCatalogPage.getTotalPages()
        );
    }
}
