package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageJpaRepository extends JpaRepository<ProductImageEntity, Long> {
    List<ProductImageEntity> findByProductEntityId(Long productVariantId);
    List<ProductImageEntity> findByProductVariantEntityId(Long productVariantId);
    List<ProductImageEntity> findByProductEntityIdInAndIsPrimaryTrue(List<Long> productId);
}
