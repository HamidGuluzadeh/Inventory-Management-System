package com.auspify_tech.inventory_management_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DisposalRequestDto(@NotBlank(message = "Product ID cannot be empty!")
                                 String productId,
                                 @NotNull(message = "Unit cost must be entered!")
                                 @Positive(message = "Unit cost must be positive!")
                                 BigDecimal unitCost,
                                 @NotNull(message = "Disposal quantity must be entered!")
                                 @Min(value = 1, message = "Disposal quantity must be at least 1!")
                                 Integer quantity,
                                 @NotBlank(message = "Disposal reason must be entered!")
                                 String disposalReason) {
}
