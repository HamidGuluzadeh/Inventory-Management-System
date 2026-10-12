package com.auspify_tech.inventory_management_system.dto.request;

import com.auspify_tech.inventory_management_system.model.OperationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StockUpdateRequestDto(@NotBlank(message = "Product ID cannot be empty!")
                                    String productId,
                                    @NotNull(message = "Quantity must be entered!")
                                    @Min(value = 1, message = "Quantity must be at leats 1!")
                                    Integer quantity,
                                    @NotNull(message = "Transaction type must be entered!")
                                    OperationType transactionType,
                                    String notes) {

}
