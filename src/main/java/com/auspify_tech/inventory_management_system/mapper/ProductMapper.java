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

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "currentQuantity", ignore = true)
    @Mapping(target = "minStockLevel", ignore = true)
    ProductResponseDto mapEntityToResponse(ProductEntity productEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductEntity mapRequestToEntity(ProductRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ProductRequestDto request, @MappingTarget ProductEntity productEntity);

    @Mapping(target = "id", source = "productEntity.id")
    @Mapping(target = "categoryId", source = "productEntity.category.id")
    @Mapping(target = "categoryName", source = "productEntity.category.name")
    @Mapping(target = "currentQuantity", source = "inventoryStockEntity.currentQuantity")
    @Mapping(target = "minStockLevel", source = "inventoryStockEntity.minStockLevel")
    @Mapping(target = "createdAt", source = "productEntity.createdAt")
    @Mapping(target = "updatedAt", source = "productEntity.updatedAt")
    ProductResponseDto mapEntityToResponseWithStock(ProductEntity productEntity, InventoryStockEntity inventoryStockEntity);
}
