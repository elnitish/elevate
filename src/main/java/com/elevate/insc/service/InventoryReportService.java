package com.elevate.insc.service;

import com.elevate.insc.dto.InventoryValuationDTO;
import com.elevate.insc.entity.StockLevelClass;
import com.elevate.insc.repository.StockLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InventoryReportService {

    private final StockLevelRepository stockLevelRepository;

    @Autowired
    public InventoryReportService(StockLevelRepository stockLevelRepository) {
        this.stockLevelRepository = stockLevelRepository;
    }

    @Transactional(readOnly = true)
    public InventoryValuationDTO getInventoryValuation(String tenantId) {
        List<StockLevelClass> stockLevels = stockLevelRepository.findByTenantId(tenantId);

        long totalProducts = 0;
        long totalUnits = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalRetail = BigDecimal.ZERO;

        for (StockLevelClass sl : stockLevels) {
            if (sl.getQuantity() <= 0) continue;
            if (sl.getProduct() == null) continue;

            totalProducts++;
            totalUnits += sl.getQuantity();

            BigDecimal qty = BigDecimal.valueOf(sl.getQuantity());
            totalCost = totalCost.add(qty.multiply(sl.getProduct().getCostPrice()));
            totalRetail = totalRetail.add(qty.multiply(sl.getProduct().getSellingPrice()));
        }

        return new InventoryValuationDTO(totalProducts, totalUnits, totalCost, totalRetail, totalRetail.subtract(totalCost));
    }
}
