package com.auspify_tech.inventory_management_system.service.implement;

import com.auspify_tech.inventory_management_system.dto.request.DisposalRequestDto;
import com.auspify_tech.inventory_management_system.dto.request.InboundRequestDto;
import com.auspify_tech.inventory_management_system.dto.request.OutboundRequestDto;
import com.auspify_tech.inventory_management_system.dto.request.StockUpdateRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.InventoryStockResponseDto;
import com.auspify_tech.inventory_management_system.dto.response.InventoryTransactionResponseDto;
import com.auspify_tech.inventory_management_system.entity.InventoryStockEntity;
import com.auspify_tech.inventory_management_system.entity.InventoryTransactionEntity;
import com.auspify_tech.inventory_management_system.exception.ConflictException;
import com.auspify_tech.inventory_management_system.exception.ResourceNotFoundException;
import com.auspify_tech.inventory_management_system.mapper.InventoryStockMapper;
import com.auspify_tech.inventory_management_system.mapper.InventoryTransactionMapper;
import com.auspify_tech.inventory_management_system.model.OperationType;
import com.auspify_tech.inventory_management_system.repository.InventoryStockRepository;
import com.auspify_tech.inventory_management_system.repository.InventoryTransactionRepository;
import com.auspify_tech.inventory_management_system.repository.ProductRepository;
import com.auspify_tech.inventory_management_system.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryServiceImpl implements InventoryService {
    InventoryStockRepository inventoryStockRepository;
    InventoryTransactionRepository inventoryTransactionRepository;
    ProductRepository productRepository;
    InventoryStockMapper inventoryStockMapper;
    InventoryTransactionMapper inventoryTransactionMapper;

    @Override
    public Page<InventoryTransactionResponseDto> getTransactionHistory(OperationType operationType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<InventoryTransactionEntity> inventoryTransactionEntities =
                inventoryTransactionRepository.findByTransactionType(operationType, pageable);

        return inventoryTransactionEntities.map(inventoryTransactionMapper::mapEntityToResponse);
    }

    @Override
    public Page<InventoryStockResponseDto> getLowStockProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<InventoryStockEntity> inventoryStockEntities = inventoryStockRepository.findLowStockProducts(pageable);

        return inventoryStockEntities.map(inventoryStockMapper::mapEntityToResponse);
    }

    @Override
    public InventoryStockResponseDto getProductStockById(String productId) {
        InventoryStockEntity stock = inventoryStockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock record not found!"));

        return inventoryStockMapper.mapEntityToResponse(stock);
    }

    @Override
    @Transactional
    public InventoryStockResponseDto processInbound(InboundRequestDto request) {
        InventoryStockEntity stock = inventoryStockRepository.findByProductId(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock record not found!"));

        stock.setCurrentQuantity(stock.getCurrentQuantity() + request.quantity());
        stock.setUpdatedAt(Instant.now());
        InventoryStockEntity updatedStock = inventoryStockRepository.save(stock);

        inventoryTransactionRepository.save(InventoryTransactionEntity.builder()
                .product(stock.getProduct())
                .transactionType(OperationType.INBOUND)
                .unitPrice(request.unitPrice())
                .quantity(request.quantity())
                .transactionDate(Instant.now())
                .notes("Supplier: " + request.supplierInfo()
                        + (request.notes() != null && !request.notes().isBlank() ? ". Note: " + request.notes() : ""))
                .build());

        return inventoryStockMapper.mapEntityToResponse(updatedStock);
    }

    @Override
    @Transactional
    public InventoryStockResponseDto processOutbound(OutboundRequestDto request) {
        InventoryStockEntity stock = inventoryStockRepository.findByProductId(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock record not found!"));

        if (stock.getCurrentQuantity() < request.quantity()) {
            throw new ConflictException("Stock not enough! Existing: " + stock.getCurrentQuantity());
        }

        stock.setCurrentQuantity(stock.getCurrentQuantity() - request.quantity());
        stock.setUpdatedAt(Instant.now());
        InventoryStockEntity updatedStock = inventoryStockRepository.save(stock);

        inventoryTransactionRepository.save(InventoryTransactionEntity.builder()
                .product(stock.getProduct())
                .transactionType(OperationType.OUTBOUND)
                .unitPrice(request.unitPrice())
                .quantity(request.quantity())
                .transactionDate(Instant.now())
                .notes("Invoice: " + request.orderReference()
                        + (request.notes() != null && !request.notes().isBlank() ? ". Note: " + request.notes() : ""))
                .build());

        return inventoryStockMapper.mapEntityToResponse(updatedStock);
    }

    @Override
    @Transactional
    public InventoryStockResponseDto processAdjustment(StockUpdateRequestDto request) {
        InventoryStockEntity stock = inventoryStockRepository.findByProductId(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock record not found!"));

        int oldQuantity = stock.getCurrentQuantity();
        int newQuantity = request.actualQuantity();
        int difference = Math.abs(newQuantity - oldQuantity);

        if (difference == 0) {
            throw new ConflictException("Actual quantity is the same as the current stock: " + oldQuantity);
        }

        stock.setCurrentQuantity(newQuantity);
        stock.setUpdatedAt(Instant.now());
        InventoryStockEntity updatedStock = inventoryStockRepository.save(stock);

        inventoryTransactionRepository.save(InventoryTransactionEntity.builder()
                .product(stock.getProduct())
                .transactionType(OperationType.ADJUSTMENT)
                .unitPrice(request.unitPrice())
                .quantity(difference)
                .transactionDate(Instant.now())
                .notes("Reason: " + request.adjustmentReason() + ". Previous: " + oldQuantity + ", New: " + newQuantity)
                .build());

        return inventoryStockMapper.mapEntityToResponse(updatedStock);
    }

    @Override
    @Transactional
    public InventoryStockResponseDto processDisposal(DisposalRequestDto request) {
        InventoryStockEntity stock = inventoryStockRepository.findByProductId(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock record not found!"));

        if (stock.getCurrentQuantity() < request.quantity()) {
            throw new ConflictException("Stock not enough! Existing: " + stock.getCurrentQuantity());
        }

        stock.setCurrentQuantity(stock.getCurrentQuantity() - request.quantity());
        stock.setUpdatedAt(Instant.now());
        InventoryStockEntity updatedStock = inventoryStockRepository.save(stock);

        inventoryTransactionRepository.save(InventoryTransactionEntity.builder()
                .product(stock.getProduct())
                .transactionType(OperationType.DISPOSAL)
                .unitPrice(request.unitCost())
                .quantity(request.quantity())
                .transactionDate(Instant.now())
                .notes("Disposal reason: " + request.disposalReason())
                .build());

        return inventoryStockMapper.mapEntityToResponse(updatedStock);
    }
}