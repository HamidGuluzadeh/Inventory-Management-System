package com.auspify_tech.inventory_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(@NotBlank(message = "Name cannot be empty!")
                                 String name,
                                 String description,
                                 String parentId) {

}
