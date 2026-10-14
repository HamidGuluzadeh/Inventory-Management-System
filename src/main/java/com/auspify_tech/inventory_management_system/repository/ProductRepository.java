package com.auspify_tech.inventory_management_system.repository;

import com.auspify_tech.inventory_management_system.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity,String> {

    boolean existsByCategoryId(String categoryId);

    @Query("SELECT p FROM ProductEntity p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ProductEntity> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    Page<ProductEntity> findAllByCategoryId(String categoryId, Pageable pageable);

    Optional<ProductEntity> findBySku(String sku);

    boolean existsBySku(String sku);

}
