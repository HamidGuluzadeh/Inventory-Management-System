package com.auspify_tech.inventory_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "inventory_stocks")
@Setter
@Getter
public class InventoryStockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false, unique = true)
    ProductEntity product;

    @Column(name = "current_quantity", nullable = false)
    Integer currentQuantity;

    @Column(name = "min_stock_level", nullable = false)
    Integer minStockLevel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @Column(name = "updated_at")
    Instant updatedAt;
}
