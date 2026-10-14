package com.auspify_tech.inventory_management_system.service.implement;

import com.auspify_tech.inventory_management_system.dto.request.ProductRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.ProductResponseDto;
import com.auspify_tech.inventory_management_system.entity.CategoryEntity;
import com.auspify_tech.inventory_management_system.entity.InventoryStockEntity;
import com.auspify_tech.inventory_management_system.entity.InventoryTransactionEntity;
import com.auspify_tech.inventory_management_system.entity.ProductEntity;
import com.auspify_tech.inventory_management_system.exception.ConflictException;
import com.auspify_tech.inventory_management_system.exception.ResourceAlreadyExistsException;
import com.auspify_tech.inventory_management_system.exception.ResourceNotFoundException;
import com.auspify_tech.inventory_management_system.mapper.ProductMapper;
import com.auspify_tech.inventory_management_system.model.OperationType;
import com.auspify_tech.inventory_management_system.repository.CategoryRepository;
import com.auspify_tech.inventory_management_system.repository.InventoryStockRepository;
import com.auspify_tech.inventory_management_system.repository.InventoryTransactionRepository;
import com.auspify_tech.inventory_management_system.repository.ProductRepository;
import com.auspify_tech.inventory_management_system.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    InventoryStockRepository inventoryStockRepository;
    InventoryTransactionRepository inventoryTransactionRepository;
    ProductMapper productMapper;

    @Override
    public Page<ProductResponseDto> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ProductEntity> productEntities = productRepository.findAll(pageable);

        return productEntities.map(productMapper::mapEntityToResponse);
    }

    @Override
    public Page<ProductResponseDto> getProductsByCategory(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ProductEntity> productEntities = productRepository.findAllByCategoryId(categoryId, pageable);

        return productEntities.map(productMapper::mapEntityToResponse);
    }

    @Override
    public Page<ProductResponseDto> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ProductEntity> productEntities = productRepository.searchProducts(keyword, pageable);

        return productEntities.map(productMapper::mapEntityToResponse);
    }

    @Override
    public ProductResponseDto getProductBySku(String sku) {
        ProductEntity productEntity = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product with SKU: " + sku + " not found!"));

        return productMapper.mapEntityToResponse(productEntity);
    }

    @Override
    public ProductResponseDto getProductById(String productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

        return productMapper.mapEntityToResponse(productEntity);
    }

    @Override
    @Transactional
    public ProductResponseDto addProduct(ProductRequestDto request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new ResourceAlreadyExistsException("Product with SKU: " + request.sku() + " already exists!");
        }

        CategoryEntity categoryEntity = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        ProductEntity productEntity = productMapper.mapRequestToEntity(request);
        productEntity.setCategory(categoryEntity);
        productEntity.setCreatedAt(Instant.now());

        ProductEntity savedProductEntity = productRepository.save(productEntity);

        InventoryStockEntity inventoryStockEntity = InventoryStockEntity.builder()
                .product(savedProductEntity)
                .currentQuantity(request.initialQuantity() != null ? request.initialQuantity() : 0)
                .minStockLevel(request.minStockLevel() != null ? request.minStockLevel() : 0)
                .build();

        inventoryStockRepository.save(inventoryStockEntity);

        if (inventoryStockEntity.getCurrentQuantity() > 0) {
            BigDecimal totalAmount = request.price().multiply(BigDecimal.valueOf(inventoryStockEntity.getCurrentQuantity()));

            InventoryTransactionEntity transaction = InventoryTransactionEntity.builder()
                    .product(savedProductEntity)
                    .transactionType(OperationType.INBOUND)
                    .quantity(inventoryStockEntity.getCurrentQuantity())
                    .notes("Məhsul sistemə əlavə edilərkən daxil edilən ilkin anbar qalığı")
                    .build();

            inventoryTransactionRepository.save(transaction);
        }

        return productMapper.mapEntityToResponseWithStock(savedProductEntity, inventoryStockEntity);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(String productId, ProductRequestDto request) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

        if (!productEntity.getSku().equalsIgnoreCase(request.sku()) && productRepository.existsBySku(request.sku())) {
            throw new ResourceAlreadyExistsException("SKU: " + request.sku() + " already in use!");
        }

        if (!productEntity.getCategory().getId().equals(request.categoryId())) {
            CategoryEntity category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
            productEntity.setCategory(category);
        }

        productMapper.updateEntityFromRequest(request, productEntity);
        ProductEntity updatedProduct = productRepository.save(productEntity);

        InventoryStockEntity inventoryStockEntity = inventoryStockRepository.findByProductId(updatedProduct.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock record of product not found!!"));

        if (request.minStockLevel() != null) {
            inventoryStockEntity.setMinStockLevel(request.minStockLevel());
            inventoryStockRepository.save(inventoryStockEntity);
        }

        return productMapper.mapEntityToResponseWithStock(updatedProduct, inventoryStockEntity);
    }

    @Override
    @Transactional
    public void deleteProduct(String productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

        InventoryStockEntity inventoryStockEntity = inventoryStockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock record of product not found!"));

        if (inventoryStockEntity.getCurrentQuantity() > 0) {
            throw new ConflictException("Product cannot be deleted due to remaining stock!");
        }

        productRepository.deleteById(productId);
    }
}
