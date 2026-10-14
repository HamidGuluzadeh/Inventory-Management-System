package com.auspify_tech.inventory_management_system.mapper;

import com.auspify_tech.inventory_management_system.dto.response.InventoryStockResponseDto;
import com.auspify_tech.inventory_management_system.entity.InventoryStockEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryStockMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "sku", source = "product.sku")
    InventoryStockResponseDto mapEntityToResponse(InventoryStockEntity inventoryStockEntity);

}
