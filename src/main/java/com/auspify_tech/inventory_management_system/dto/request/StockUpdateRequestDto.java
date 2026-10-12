package com.auspify_tech.inventory_management_system.dto.request;

import com.auspify_tech.inventory_management_system.model.OperationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record StockUpdateRequestDto(@NotBlank(message = "Product ID cannot be empty!")
                                    String productId,
                                    @NotNull(message = "Actual quantity must be entered!")
                                    @Min(value = 0, message = "Stock quantity cannot be negative!")
                                    Integer actualQuantity,
                                    @NotNull(message = "Price must be entered!")
                                    @Positive(message = "Price must be positive!")
                                    BigDecimal unitPrice,
                                    @NotBlank(message = "Stock adjustment reason must be noted!")
                                    String adjustmentReason) {

}
