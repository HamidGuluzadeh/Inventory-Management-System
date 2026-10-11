package com.auspify_tech.inventory_management_system.repository;

import com.auspify_tech.inventory_management_system.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity,String> {

}
