package com.auspify_tech.inventory_management_system.mapper;

import com.auspify_tech.inventory_management_system.dto.request.CategoryRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.CategoryResponseDto;
import com.auspify_tech.inventory_management_system.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    CategoryResponseDto mapEntityToResponse(CategoryEntity categoryEntity);

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "id", ignore = true)
    CategoryEntity mapRequestToEntity(CategoryRequestDto request);

}
