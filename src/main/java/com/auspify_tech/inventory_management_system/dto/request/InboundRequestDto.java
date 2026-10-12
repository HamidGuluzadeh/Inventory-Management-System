package com.auspify_tech.inventory_management_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record InboundRequestDto(@NotBlank(message = "Product ID cannot be empty!")
                                String productId,
                                @NotNull(message = "Unit price must be entered!")
                                @Positive(message = "Price must be positive!")
                                BigDecimal unitPrice,
                                @NotNull(message = "Quantity must be entered!")
                                @Min(value = 1, message = "Quantity must be at least 1!")
                                Integer quantity,
                                @NotBlank(message = "Supplier info cannot be empty!")
                                String supplierInfo,
                                String notes) {

}
