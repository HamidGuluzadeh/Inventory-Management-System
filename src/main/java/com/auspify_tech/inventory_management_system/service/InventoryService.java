package com.auspify_tech.inventory_management_system.service;

import com.auspify_tech.inventory_management_system.dto.request.DisposalRequestDto;
import com.auspify_tech.inventory_management_system.dto.request.InboundRequestDto;
import com.auspify_tech.inventory_management_system.dto.request.OutboundRequestDto;
import com.auspify_tech.inventory_management_system.dto.request.StockUpdateRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.InventoryStockResponseDto;
import com.auspify_tech.inventory_management_system.dto.response.InventoryTransactionResponseDto;
import com.auspify_tech.inventory_management_system.model.OperationType;
import org.springframework.data.domain.Page;

public interface InventoryService {

    Page<InventoryTransactionResponseDto> getTransactionHistory(OperationType operationType, int page, int size);

    Page<InventoryStockResponseDto> getLowStockProducts(int page, int size);

    InventoryStockResponseDto getProductStockById(String productId);

    InventoryStockResponseDto processInbound(InboundRequestDto request);

    InventoryStockResponseDto processOutbound(OutboundRequestDto request);

    InventoryStockResponseDto processAdjustment(StockUpdateRequestDto request);

    InventoryStockResponseDto processDisposal(DisposalRequestDto request);

}
