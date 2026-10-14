package com.auspify_tech.inventory_management_system.entity;

import com.auspify_tech.inventory_management_system.model.OperationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "inventory_transactions")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false, updatable = false)
    ProductEntity product;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 50, nullable = false, updatable = false)
    OperationType transactionType;

    @Column(name = "quantity", nullable = false, updatable = false)
    Integer quantity;

    @Column(name = "transaction_date", nullable = false, updatable = false)
    Instant transactionDate;

    @Column(name = "notes", updatable = false)
    String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @Column(name = "updated_at")
    Instant updatedAt;
}
