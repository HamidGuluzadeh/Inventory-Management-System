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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/transactions")
    public ResponseEntity<SuccessDto<Page<InventoryTransactionResponseDto>>> getTransactionHistory(@RequestParam OperationType operationType,
                                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                                   @RequestParam(defaultValue = "20") int size) {
        Page<InventoryTransactionResponseDto> response = inventoryService.getTransactionHistory(operationType, page, size);
        SuccessDto<Page<InventoryTransactionResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<SuccessDto<Page<InventoryStockResponseDto>>> getLowStockProducts(@RequestParam(defaultValue = "0") int page,
                                                                                           @RequestParam(defaultValue = "20") int size) {
        Page<InventoryStockResponseDto> response = inventoryService.getLowStockProducts(page, size);
        SuccessDto<Page<InventoryStockResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/product/{productId}/stock")
    public ResponseEntity<SuccessDto<InventoryStockResponseDto>> getProductStockById(@PathVariable String productId) {
        InventoryStockResponseDto response = inventoryService.getProductStockById(productId);
        SuccessDto<InventoryStockResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/operations/inbound")
    public ResponseEntity<SuccessDto<InventoryStockResponseDto>> processInbound(@RequestBody @Valid InboundRequestDto request) {
        InventoryStockResponseDto response = inventoryService.processInbound(request);
        SuccessDto<InventoryStockResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/operations/outbound")
    public ResponseEntity<SuccessDto<InventoryStockResponseDto>> processOutbound(@RequestBody @Valid OutboundRequestDto request) {
        InventoryStockResponseDto response = inventoryService.processOutbound(request);
        SuccessDto<InventoryStockResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/operations/adjustment")
    public ResponseEntity<SuccessDto<InventoryStockResponseDto>> processAdjustment(@RequestBody @Valid StockUpdateRequestDto request) {
        InventoryStockResponseDto response = inventoryService.processAdjustment(request);
        SuccessDto<InventoryStockResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/operations/disposal")
    public ResponseEntity<SuccessDto<InventoryStockResponseDto>> processDisposal(@RequestBody @Valid DisposalRequestDto request) {
        InventoryStockResponseDto response = inventoryService.processDisposal(request);
        SuccessDto<InventoryStockResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }
}
