package com.auspify_tech.inventory_management_system.mapper;

import com.auspify_tech.inventory_management_system.dto.request.ProductRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.ProductResponseDto;
import com.auspify_tech.inventory_management_system.entity.InventoryStockEntity;
import com.auspify_tech.inventory_management_system.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    ProductResponseDto mapEntityToResponse(ProductEntity productEntity);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "id", ignore = true)
    ProductEntity mapRequestToEntity(ProductRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "sku", ignore = true)
    void updateEntityFromRequest(ProductRequestDto request, @MappingTarget ProductEntity productEntity);

    @Mapping(target = "id", ignore = true)
    ProductResponseDto mapEntityToResponseWithStock(ProductEntity productEntity, InventoryStockEntity inventoryStockEntity);

}
