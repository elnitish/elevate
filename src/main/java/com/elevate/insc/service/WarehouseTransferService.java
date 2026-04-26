package com.elevate.insc.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.config.InsufficientStockException;
import com.elevate.insc.dto.WarehouseTransferReqDTO;
import com.elevate.insc.dto.WarehouseTransferResDTO;
import com.elevate.insc.entity.StockMovementClass;
import com.elevate.insc.entity.WarehouseTransferClass;
import com.elevate.insc.repository.WarehouseRepository;
import com.elevate.insc.repository.WarehouseTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WarehouseTransferService {

    private final WarehouseTransferRepository transferRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockLevelService stockLevelService;
    private final StockMovementService stockMovementService;

    @Autowired
    public WarehouseTransferService(WarehouseTransferRepository transferRepository,
                                     WarehouseRepository warehouseRepository,
                                     StockLevelService stockLevelService,
                                     StockMovementService stockMovementService) {
        this.transferRepository = transferRepository;
        this.warehouseRepository = warehouseRepository;
        this.stockLevelService = stockLevelService;
        this.stockMovementService = stockMovementService;
    }

    @Transactional
    public ApiResponse<?> createTransfer(String tenantId, String initiatedBy, WarehouseTransferReqDTO dto) {
        if (dto.getFromWarehouseId().equals(dto.getToWarehouseId())) {
            return new ApiResponse<>("Source and destination warehouse cannot be the same", 400, null);
        }

        if (warehouseRepository.findByTenantIdAndId(tenantId, dto.getFromWarehouseId()).isEmpty()) {
            return new ApiResponse<>("Source warehouse not found", 404, null);
        }
        if (warehouseRepository.findByTenantIdAndId(tenantId, dto.getToWarehouseId()).isEmpty()) {
            return new ApiResponse<>("Destination warehouse not found", 404, null);
        }

        WarehouseTransferClass transfer = new WarehouseTransferClass();
        transfer.setId(java.util.UUID.randomUUID().toString());
        transfer.setTenantId(tenantId);
        transfer.setFromWarehouseId(dto.getFromWarehouseId());
        transfer.setToWarehouseId(dto.getToWarehouseId());
        transfer.setProductId(dto.getProductId());
        transfer.setQuantity(dto.getQuantity());
        transfer.setStatus(WarehouseTransferClass.Status.PENDING);
        transfer.setInitiatedBy(initiatedBy);
        transfer.setNotes(dto.getNotes());

        WarehouseTransferClass saved = transferRepository.save(transfer);
        return new ApiResponse<>("Transfer created successfully", 201, new WarehouseTransferResDTO(saved));
    }

    @Transactional
    public ApiResponse<?> completeTransfer(String tenantId, String transferId) {
        Optional<WarehouseTransferClass> transferOpt = transferRepository.findByTenantIdAndId(tenantId, transferId);
        if (transferOpt.isEmpty()) {
            return new ApiResponse<>("Transfer not found", 404, null);
        }

        WarehouseTransferClass transfer = transferOpt.get();
        if (transfer.getStatus() != WarehouseTransferClass.Status.PENDING
                && transfer.getStatus() != WarehouseTransferClass.Status.IN_TRANSIT) {
            return new ApiResponse<>("Transfer cannot be completed in status: " + transfer.getStatus(), 400, null);
        }

        // Decrease stock from source warehouse
        try {
            stockLevelService.decreaseStock(tenantId, transfer.getProductId(), transfer.getFromWarehouseId(), transfer.getQuantity());
        } catch (InsufficientStockException e) {
            return new ApiResponse<>("Insufficient stock in source warehouse: " + e.getMessage(), 400, null);
        }

        // Increase stock in destination warehouse
        stockLevelService.increaseStock(tenantId, transfer.getProductId(), transfer.getToWarehouseId(), transfer.getQuantity());

        // Record stock movements
        String outMovementId = java.util.UUID.randomUUID().toString();
        StockMovementClass outMovement = new StockMovementClass(
                outMovementId, tenantId, transfer.getProductId(), null, null,
                StockMovementClass.Type.TRANSFER, transfer.getQuantity(),
                "Transfer OUT: " + transfer.getId());
        outMovement.setWarehouseId(transfer.getFromWarehouseId());

        String inMovementId = java.util.UUID.randomUUID().toString();
        StockMovementClass inMovement = new StockMovementClass(
                inMovementId, tenantId, transfer.getProductId(), null, null,
                StockMovementClass.Type.TRANSFER, transfer.getQuantity(),
                "Transfer IN: " + transfer.getId());
        inMovement.setWarehouseId(transfer.getToWarehouseId());

        // Save movements via repository (direct save since we need warehouse-aware movement)
        stockMovementService.saveMovement(outMovement);
        stockMovementService.saveMovement(inMovement);

        transfer.setStatus(WarehouseTransferClass.Status.COMPLETED);
        transfer.setCompletedAt(LocalDateTime.now());
        transferRepository.save(transfer);

        return new ApiResponse<>("Transfer completed successfully", 200, new WarehouseTransferResDTO(transfer));
    }

    @Transactional
    public ApiResponse<?> cancelTransfer(String tenantId, String transferId) {
        Optional<WarehouseTransferClass> transferOpt = transferRepository.findByTenantIdAndId(tenantId, transferId);
        if (transferOpt.isEmpty()) {
            return new ApiResponse<>("Transfer not found", 404, null);
        }

        WarehouseTransferClass transfer = transferOpt.get();
        if (transfer.getStatus() == WarehouseTransferClass.Status.COMPLETED) {
            return new ApiResponse<>("Cannot cancel a completed transfer", 400, null);
        }

        transfer.setStatus(WarehouseTransferClass.Status.CANCELLED);
        transferRepository.save(transfer);
        return new ApiResponse<>("Transfer cancelled", 200, null);
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> getTransfers(String tenantId, String status, Pageable pageable) {
        Page<WarehouseTransferClass> transfers;
        if (status != null && !status.isBlank()) {
            WarehouseTransferClass.Status s = WarehouseTransferClass.Status.valueOf(status.toUpperCase());
            transfers = transferRepository.findByTenantIdAndStatusWithDetails(tenantId, s, pageable);
        } else {
            transfers = transferRepository.findByTenantIdWithDetails(tenantId, pageable);
        }

        Page<WarehouseTransferResDTO> dtos = transfers.map(WarehouseTransferResDTO::new);
        return new ApiResponse<>("Transfers retrieved successfully", 200, dtos);
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> getTransferById(String tenantId, String transferId) {
        Optional<WarehouseTransferClass> transfer = transferRepository.findByTenantIdAndId(tenantId, transferId);
        if (transfer.isEmpty()) {
            return new ApiResponse<>("Transfer not found", 404, null);
        }
        return new ApiResponse<>("Transfer retrieved", 200, new WarehouseTransferResDTO(transfer.get()));
    }
}
