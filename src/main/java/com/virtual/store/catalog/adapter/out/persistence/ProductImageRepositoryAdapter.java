package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductImageEntity;
import com.virtual.store.catalog.adapter.out.persistence.entity.ProductVariantEntity;
import com.virtual.store.catalog.adapter.out.persistence.mapper.ProductImageMapper;
import com.virtual.store.catalog.application.port.out.ProductImageRepository;
import com.virtual.store.catalog.domain.exception.ResourceNotFoundException;
import com.virtual.store.catalog.domain.model.ProductImage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductImageRepositoryAdapter implements ProductImageRepository {

    private final ProductImageJpaRepository productImageJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ProductVariantJpaRepository productVariantJpaRepository;

    public ProductImageRepositoryAdapter(ProductImageJpaRepository productImageJpaRepository, ProductJpaRepository productJpaRepository, ProductVariantJpaRepository productVariantJpaRepository){
        this.productImageJpaRepository = productImageJpaRepository;
        this.productJpaRepository = productJpaRepository;
        this.productVariantJpaRepository = productVariantJpaRepository;
    }


    @Override
    public ProductImage createProductImage(ProductImage productImage) {
        if(!productJpaRepository.existsById(productImage.productId())){
            throw new ResourceNotFoundException(" The project with id=" + productImage.productId() + " does not exist");
        }


        ProductVariantEntity productVariantEntity=null;
        if(productImage.productVariantId() != null){
            if(!productVariantJpaRepository.existsById(productImage.productVariantId())) {
                throw new ResourceNotFoundException("The Variant with Id=" + productImage.productVariantId() + " does not exist");
            }
            productVariantEntity = productVariantJpaRepository.getReferenceById(productImage.productVariantId());
        }

        ProductEntity productEntity = productJpaRepository.getReferenceById(productImage.productId());
        ProductImageEntity productImageEntity = ProductImageMapper.toEntity(productImage, productEntity, productVariantEntity);
        productImageJpaRepository.save(productImageEntity);
        return ProductImageMapper.toDomain(productImageEntity);
    }

    @Override
    public List<ProductImage> getProductImageByProductId(Long productId) {
        if(!productJpaRepository.existsById(productId)){
            throw new ResourceNotFoundException("The Product with Id=" + productId + " does not exist");
        }
        List<ProductImageEntity> productImageEntityList = productImageJpaRepository.findByProductEntityId(productId);
        return productImageEntityList.stream()
                .map(ProductImageMapper::toDomain)
                .toList();
    }

    @Override
    public List<ProductImage> getProductImageByProductVariantId(Long productVariantId) {
        if(!productVariantJpaRepository.existsById(productVariantId)){
            throw new ResourceNotFoundException("The Variant with Id=" + productVariantId + " does not exist");
        }
        List<ProductImageEntity> productImageEntityList = productImageJpaRepository.findByProductVariantEntityId(productVariantId);
        return productImageEntityList.stream()
                .map(ProductImageMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteProductImage(Long idProductImage) {
        if(!productImageJpaRepository.existsById(idProductImage)){
            throw new ResourceNotFoundException("The Product Image with Id=" + idProductImage + " does not exist");
        }
        productImageJpaRepository.deleteById(idProductImage);
    }

    @Override
    public void updateProductImage(ProductImage productImage) {
        ProductImageEntity productImageEntity = productImageJpaRepository.findById(productImage.id())
            .orElseThrow(() -> new ResourceNotFoundException("The Product Image with Id=" + productImage.id() + " does not exist"));

        productImageEntity.setUrlImage(productImage.url());
        productImageEntity.setPrimary(productImage.isPrimary());
    }
}
