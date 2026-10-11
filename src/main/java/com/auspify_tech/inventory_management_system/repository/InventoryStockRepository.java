package com.auspify_tech.inventory_management_system.repository;

import com.auspify_tech.inventory_management_system.entity.InventoryStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStockEntity, String> {

}
