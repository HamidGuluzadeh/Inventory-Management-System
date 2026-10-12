package com.auspify_tech.inventory_management_system.dto.response;

import java.time.Instant;

public record CategoryResponseDto(String id,
                                  String name,
                                  String description,
                                  String parentId,
                                  String parentName,
                                  Instant createdAt,
                                  Instant updatedAt) {

}
