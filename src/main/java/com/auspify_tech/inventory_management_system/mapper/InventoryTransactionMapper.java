package com.auspify_tech.inventory_management_system.mapper;

import com.auspify_tech.inventory_management_system.dto.response.InventoryTransactionResponseDto;
import com.auspify_tech.inventory_management_system.entity.InventoryTransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryTransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", source = "product.id")
    InventoryTransactionResponseDto mapEntityToResponse(InventoryTransactionEntity inventoryTransactionEntity);

}
