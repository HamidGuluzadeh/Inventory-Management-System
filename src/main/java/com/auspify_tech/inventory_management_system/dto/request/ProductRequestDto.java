package com.auspify_tech.inventory_management_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequestDto(@NotBlank(message = "SKU cannot be empty!")
                                String sku,
                                @NotBlank(message = "Product name cannot be empty!")
                                String name,
                                @NotBlank(message = "Category must be entered!")
                                String categoryId,
                                @NotNull(message = "Price must be entered!")
                                @Positive(message = "Price must be positive!")
                                BigDecimal price,
                                @NotNull(message = "Initial stock quantity must be entered!")
                                @Min(value = 0, message = "Stock quantity cannot be negative!")
                                Integer initialQuantity,
                                @NotNull(message = "Minimum stock level must be entered!")
                                @Min(value = 0, message = "Minimum stock level cannot be negative!")
                                Integer minStockLevel) {

}
