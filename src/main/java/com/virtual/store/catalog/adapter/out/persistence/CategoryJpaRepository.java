package com.virtual.store.catalog.adapter.out.persistence;

import com.virtual.store.catalog.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
}
