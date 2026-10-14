package com.auspify_tech.inventory_management_system.service;

import com.auspify_tech.inventory_management_system.dto.request.ProductRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.ProductResponseDto;
import org.springframework.data.domain.Page;

public interface ProductService {

    Page<ProductResponseDto> getAllProducts(int page, int size);

    Page<ProductResponseDto> getProductsByCategory(String categoryId, int page, int size);

    Page<ProductResponseDto> searchProducts(String keyword, int page, int size);

    ProductResponseDto getProductBySku(String sku);

    ProductResponseDto getProductById(String productId);

    ProductResponseDto addProduct(ProductRequestDto request);

    ProductResponseDto updateProduct(String productId, ProductRequestDto request);

    void deleteProduct(String productId);

}
