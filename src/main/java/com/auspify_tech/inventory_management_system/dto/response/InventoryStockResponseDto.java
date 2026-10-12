package com.auspify_tech.inventory_management_system.dto.response;

import java.time.Instant;

public record InventoryStockResponseDto(String id,
                                        String productId,
                                        String sku,
                                        String productName,
                                        Integer currentQuantity,
                                        Integer minStockLevel,
                                        Boolean isLowStock,
                                        Instant createdAt,
                                        Instant updatedAt) {

}
