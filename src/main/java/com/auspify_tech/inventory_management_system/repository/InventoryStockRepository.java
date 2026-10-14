package com.auspify_tech.inventory_management_system.repository;

import com.auspify_tech.inventory_management_system.entity.InventoryStockEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStockEntity, String> {

    Optional<InventoryStockEntity> findByProductId(String productId);

    @Query("SELECT s FROM InventoryStockEntity s WHERE s.currentQuantity <= s.minStockLevel")
    Page<InventoryStockEntity> findLowStockProducts(Pageable pageable);

}
