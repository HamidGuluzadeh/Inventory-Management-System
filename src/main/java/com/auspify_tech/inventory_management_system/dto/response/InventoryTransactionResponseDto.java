package com.auspify_tech.inventory_management_system.dto.response;

import com.auspify_tech.inventory_management_system.model.OperationType;

import java.time.Instant;

public record InventoryTransactionResponseDto(String id,
                                              String productId,
                                              String productName,
                                              String categoryId,
                                              String categoryName,
                                              OperationType transactionType,
                                              Integer quantity,
                                              Instant transactionDate,
                                              Instant createdAt) {

}
