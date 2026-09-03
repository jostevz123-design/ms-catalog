package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.ProductEntity;
import com.virtual.store.catalog.adapter.out.persistence.projection.ProductCatalogProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    @Query(value = """
            SELECT p.id as id, p.name as name, p.brand as brand, MIN(v.price) as minPrice
            FROM ProductEntity p JOIN p.productVariantEntityList v
            WHERE p.active = true
            GROUP BY p.id, p.name, p.brand
            """,
            countQuery = "SELECT COUNT(DISTINCT p.id) FROM ProductEntity p JOIN p.productVariantEntityList v WHERE p.active = true")
    Page<ProductCatalogProjection> findCatalog(Pageable pageable);

    @Query( value = """
            SELECT p.id as id, p.name as name, p.brand as brand, MIN(v.price) as minPrice
            FROM ProductEntity p JOIN p.productVariantEntityList v
            WHERE p.active = true AND p.categoryEntity.id = :categoryId
            GROUP BY p.id, p.name, p.brand
           """,
            countQuery = """
                    SELECT COUNT(DISTINCT p.id) 
                    FROM ProductEntity p 
                    JOIN p.productVariantEntityList v 
                    WHERE p.active = true AND p.categoryEntity.id = :categoryId
                    """)
    Page<ProductCatalogProjection> findByCategoryEntityId(Long categoryId, Pageable pageable);
}
