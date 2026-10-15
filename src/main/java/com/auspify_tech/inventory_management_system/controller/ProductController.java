package com.auspify_tech.inventory_management_system.controller;

import com.auspify_tech.inventory_management_system.dto.SuccessDto;
import com.auspify_tech.inventory_management_system.dto.request.ProductRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.ProductResponseDto;
import com.auspify_tech.inventory_management_system.model.SuccessStatus;
import com.auspify_tech.inventory_management_system.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<SuccessDto<Page<ProductResponseDto>>> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "20") int size) {
        Page<ProductResponseDto> response = productService.getAllProducts(page, size);
        SuccessDto<Page<ProductResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<SuccessDto<Page<ProductResponseDto>>> getProductsByCategory(@PathVariable String categoryId,
                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "20") int size) {
        Page<ProductResponseDto> response = productService.getProductsByCategory(categoryId, page, size);
        SuccessDto<Page<ProductResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<SuccessDto<Page<ProductResponseDto>>> searchProducts(@RequestParam String keyword,
                                                                               @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "20") int size) {
        Page<ProductResponseDto> response = productService.searchProducts(keyword, page, size);
        SuccessDto<Page<ProductResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<SuccessDto<ProductResponseDto>> getProductBySku(@PathVariable String sku) {
        ProductResponseDto response = productService.getProductBySku(sku);
        SuccessDto<ProductResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<SuccessDto<ProductResponseDto>> getProductById(@PathVariable String productId) {
        ProductResponseDto response = productService.getProductById(productId);
        SuccessDto<ProductResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<SuccessDto<ProductResponseDto>> addProduct(@RequestBody @Valid ProductRequestDto request) {
        ProductResponseDto response = productService.addProduct(request);
        SuccessDto<ProductResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<SuccessDto<ProductResponseDto>> updateProduct(@PathVariable String productId,
                                                                        @RequestBody @Valid ProductRequestDto request) {
        ProductResponseDto response = productService.updateProduct(productId, request);
        SuccessDto<ProductResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
