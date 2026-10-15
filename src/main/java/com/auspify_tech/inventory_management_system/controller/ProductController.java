package com.auspify_tech.inventory_management_system.controller;

import com.auspify_tech.inventory_management_system.dto.SuccessDto;
import com.auspify_tech.inventory_management_system.dto.request.ProductRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.ProductResponseDto;
import com.auspify_tech.inventory_management_system.model.SuccessStatus;
import com.auspify_tech.inventory_management_system.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "APIs for managing catalog products, SKU lookups, category filtering, and product searches")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/all")
    @Operation(summary = "Get all products", description = "Retrieves a paginated list of all products in the catalog")
    public ResponseEntity<SuccessDto<Page<ProductResponseDto>>> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "20") int size) {
        Page<ProductResponseDto> response = productService.getAllProducts(page, size);
        SuccessDto<Page<ProductResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieves a paginated list of products associated with a specific category ID")
    public ResponseEntity<SuccessDto<Page<ProductResponseDto>>> getProductsByCategory(@PathVariable String categoryId,
                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "20") int size) {
        Page<ProductResponseDto> response = productService.getProductsByCategory(categoryId, page, size);
        SuccessDto<Page<ProductResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Searches products matching the provided keyword against product name or description")
    public ResponseEntity<SuccessDto<Page<ProductResponseDto>>> searchProducts(@RequestParam String keyword,
                                                                               @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "20") int size) {
        Page<ProductResponseDto> response = productService.searchProducts(keyword, page, size);
        SuccessDto<Page<ProductResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU", description = "Retrieves a single product using its unique Stock Keeping Unit (SKU)")
    public ResponseEntity<SuccessDto<ProductResponseDto>> getProductBySku(@PathVariable String sku) {
        ProductResponseDto response = productService.getProductBySku(sku);
        SuccessDto<ProductResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID", description = "Retrieves a single product's information using its unique identifier")
    public ResponseEntity<SuccessDto<ProductResponseDto>> getProductById(@PathVariable String productId) {
        ProductResponseDto response = productService.getProductById(productId);
        SuccessDto<ProductResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/new")
    @Operation(summary = "Add new product", description = "Registers a new product in the catalog and initializes its inventory stock record")
    public ResponseEntity<SuccessDto<ProductResponseDto>> addProduct(@RequestBody @Valid ProductRequestDto request) {
        ProductResponseDto response = productService.addProduct(request);
        SuccessDto<ProductResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product", description = "Updates an existing product's catalog metadata and threshold parameters")
    public ResponseEntity<SuccessDto<ProductResponseDto>> updateProduct(@PathVariable String productId,
                                                                        @RequestBody @Valid ProductRequestDto request) {
        ProductResponseDto response = productService.updateProduct(productId, request);
        SuccessDto<ProductResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product", description = "Removes a product from the system provided if its remaining stock is zero.")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
