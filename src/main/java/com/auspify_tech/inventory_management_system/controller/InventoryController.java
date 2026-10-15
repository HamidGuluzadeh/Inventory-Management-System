package com.auspify_tech.inventory_management_system.controller;

import com.auspify_tech.inventory_management_system.dto.SuccessDto;
import com.auspify_tech.inventory_management_system.dto.request.DisposalRequestDto;
import com.auspify_tech.inventory_management_system.dto.request.InboundRequestDto;
import com.auspify_tech.inventory_management_system.dto.request.OutboundRequestDto;
import com.auspify_tech.inventory_management_system.dto.request.StockUpdateRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.InventoryStockResponseDto;
import com.auspify_tech.inventory_management_system.dto.response.InventoryTransactionResponseDto;
import com.auspify_tech.inventory_management_system.model.OperationType;
import com.auspify_tech.inventory_management_system.model.SuccessStatus;
import com.auspify_tech.inventory_management_system.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Controller",
        description = "APIs for warehouse stock operations: inbound receiving, outbound order processing, stock adjustments, disposals, low-stock monitoring, and audit logs")
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/transactions")
    @Operation(summary = "Get transaction history", description = "Retrieves paginated audit logs of all inventory movements, optionally filtered by operation type")
    public ResponseEntity<SuccessDto<Page<InventoryTransactionResponseDto>>> getTransactionHistory(@RequestParam OperationType operationType,
                                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                                   @RequestParam(defaultValue = "20") int size) {
        Page<InventoryTransactionResponseDto> response = inventoryService.getTransactionHistory(operationType, page, size);
        SuccessDto<Page<InventoryTransactionResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low-stock products", description = "Retrieves a paginated list of inventory stock records where current quantity is equal to or below the minimum stock threshold")
    public ResponseEntity<SuccessDto<Page<InventoryStockResponseDto>>> getLowStockProducts(@RequestParam(defaultValue = "0") int page,
                                                                                           @RequestParam(defaultValue = "20") int size) {
        Page<InventoryStockResponseDto> response = inventoryService.getLowStockProducts(page, size);
        SuccessDto<Page<InventoryStockResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/product/{productId}/stock")
    @Operation(summary = "Get product stock level", description = "Retrieves current inventory balance and threshold limits for a specific product ID")
    public ResponseEntity<SuccessDto<InventoryStockResponseDto>> getProductStockById(@PathVariable String productId) {
        InventoryStockResponseDto response = inventoryService.getProductStockById(productId);
        SuccessDto<InventoryStockResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/operations/inbound")
    @Operation(summary = "Process inbound receiving", description = "Increases product stock quantity upon supplier delivery and logs an INBOUND transaction record")
    public ResponseEntity<SuccessDto<InventoryStockResponseDto>> processInbound(@RequestBody @Valid InboundRequestDto request) {
        InventoryStockResponseDto response = inventoryService.processInbound(request);
        SuccessDto<InventoryStockResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/operations/outbound")
    @Operation(summary = "Process outbound shipment", description = "Deducts stock for sales orders/shipments after verifying sufficient balance and logs an OUTBOUND transaction record")
    public ResponseEntity<SuccessDto<InventoryStockResponseDto>> processOutbound(@RequestBody @Valid OutboundRequestDto request) {
        InventoryStockResponseDto response = inventoryService.processOutbound(request);
        SuccessDto<InventoryStockResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/operations/adjustment")
    @Operation(summary = "Process stock adjustment", description = "Reconciles actual physical stock count with system stock and logs an ADJUSTMENT transaction record")
    public ResponseEntity<SuccessDto<InventoryStockResponseDto>> processAdjustment(@RequestBody @Valid StockUpdateRequestDto request) {
        InventoryStockResponseDto response = inventoryService.processAdjustment(request);
        SuccessDto<InventoryStockResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/operations/disposal")
    @Operation(summary = "Process stock disposal", description = "Deducts stock due to damage, expiry, or waste and logs a DISPOSAL transaction record")
    public ResponseEntity<SuccessDto<InventoryStockResponseDto>> processDisposal(@RequestBody @Valid DisposalRequestDto request) {
        InventoryStockResponseDto response = inventoryService.processDisposal(request);
        SuccessDto<InventoryStockResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }
}
