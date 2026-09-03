package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantJpaRepository extends JpaRepository<ProductVariantEntity, Long> {

    List<ProductVariantEntity> findByProductEntityId(Long productId);
}
