package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductVariantEntity;
import com.virtual.store.catalog.adapter.out.persistence.mapper.ProductVariantMapper;
import com.virtual.store.catalog.application.port.out.ProductVariantRepository;
import com.virtual.store.catalog.domain.exception.ResourceNotFoundException;
import com.virtual.store.catalog.domain.model.ProductVariant;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductVariantRepositoryAdapter implements ProductVariantRepository {

    private final ProductVariantJpaRepository productVariantJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    public ProductVariantRepositoryAdapter(ProductVariantJpaRepository productVariantJpaRepository, ProductJpaRepository productJpaRepository){
        this.productVariantJpaRepository = productVariantJpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public ProductVariant createProductVariant(ProductVariant productVariant) {
        Long productId = productVariant.productId();
        if(!productJpaRepository.existsById(productId)){
            throw new ResourceNotFoundException(" Product with id " + productId + " does not exist");
        }
        ProductEntity productEntity = productJpaRepository.getReferenceById(productId);
        ProductVariantEntity productVariantEntity = ProductVariantMapper.toEntity(productVariant, productEntity);
        productVariantJpaRepository.save(productVariantEntity);
        return ProductVariantMapper.toDomain(productVariantEntity);
    }

    @Override
    public void updateProductVariant(ProductVariant productVariant) {
        ProductVariantEntity productVariantEntity = productVariantJpaRepository.findById(productVariant.id())
                .orElseThrow(()-> new ResourceNotFoundException(" The Variant with id:" + productVariant.id() + " does not exist"));
        productVariantEntity.setPrice(productVariant.price());
        productVariantEntity.setAttributes(productVariant.attributes());
        productVariantEntity.setStock(productVariant.stock());
    }

    @Override
    public void updateVariantStock(Long variantId, Long stock) {
        ProductVariantEntity productVariantEntity = productVariantJpaRepository.findById(variantId)
                .orElseThrow(()-> new ResourceNotFoundException(" The Variant with id:" + variantId + " does not exist"));
        productVariantEntity.setStock(stock);
    }

    @Override
    public List<ProductVariant> getVariantsByProductId(Long productId) {
        if(!productJpaRepository.existsById(productId)){
            throw new ResourceNotFoundException(" Product with id " + productId + " does not exist");
        }
        List<ProductVariantEntity> productVariantEntityList = productVariantJpaRepository.findByProductEntityId(productId);
        return productVariantEntityList.stream()
                .map(ProductVariantMapper::toDomain)
                .toList();
    }
}
