package com.auspify_tech.inventory_management_system.repository;

import com.auspify_tech.inventory_management_system.entity.InventoryTransactionEntity;
import com.auspify_tech.inventory_management_system.model.OperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransactionEntity, String> {

    Page<InventoryTransactionEntity> findByProductId(String productId, Pageable pageable);

    boolean existsByProductId(String productId);

    Page<InventoryTransactionEntity> findByTransactionType(OperationType operationType, Pageable pageable);

}
