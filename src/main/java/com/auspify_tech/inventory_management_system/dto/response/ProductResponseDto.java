package com.auspify_tech.inventory_management_system.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponseDto(String id,
                                 String sku,
                                 String name,
                                 BigDecimal price,
                                 String categoryId,
                                 String categoryName,
                                 Integer currentQuantity,
                                 Integer minStockLevel,
                                 Instant createdAt,
                                 Instant updatedAt) {

}
