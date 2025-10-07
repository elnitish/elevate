package com.elevate.fna.service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.InvoiceItemReqDTO;
import com.elevate.fna.dto.InvoiceReqDTO;
import com.elevate.fna.dto.InvoiceResDTO;
import com.elevate.fna.dto.PaymentClassReqDTO;
import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.entity.InvoiceItemsClass;
import com.elevate.fna.repository.InvoiceClassRepo;
import com.elevate.fna.repository.PaymentClassRepo;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.repository.ProductClassRepo;
import com.elevate.insc.service.InventoryService;
import com.elevate.insc.service.StockMovementService;
import com.elevate.insc.service.StockLevelService;

@Service
public class InvoiceService {

    private final InvoiceClassRepo invoiceClassRepo;
    private final ProductClassRepo productClassRepo;
    private PaymentClassRepo paymentClassRepo;
    private InventoryService inventoryService;
    private StockMovementService stockMovementService;
    private StockLevelService stockLevelService;

    @Autowired
    public InvoiceService(InvoiceClassRepo invoiceClassRepo,
                          ProductClassRepo productClassRepo,
                          PaymentClassRepo paymentClassRepo,
                          InventoryService inventoryService,
                          StockMovementService stockMovementService,
                          StockLevelService stockLevelService) {

        this.invoiceClassRepo = invoiceClassRepo;
        this.productClassRepo = productClassRepo;
        this.paymentClassRepo = paymentClassRepo;
        this.inventoryService = inventoryService;
        this.stockMovementService = stockMovementService;
        this.stockLevelService = stockLevelService;
    }

    public ApiResponse<?> createNewInvoice(InvoiceReqDTO dto) {
        // Validate input
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            return new ApiResponse<>("Invoice must contain at least one item", 400, null);
        }

        // Validate all products exist and check stock availability
        for (InvoiceItemReqDTO itemDTO : dto.getItems()) {
            Optional<ProductClass> productOpt = productClassRepo.findById(itemDTO.getProductId());
            if (productOpt.isEmpty()) {
                return new ApiResponse<>("Product with ID " + itemDTO.getProductId() + " not found", 404, null);
            }
            
            ProductClass product = productOpt.get();
            // Validate product belongs to the same tenant
            if (!product.getTenantId().equals(dto.getTenantId())) {
                return new ApiResponse<>("Product does not belong to this tenant", 403, null);
            }
            
            // Check stock availability
            if (!stockLevelService.hasSufficientStock(dto.getTenantId(), itemDTO.getProductId(), itemDTO.getQuantity())) {
                return new ApiResponse<>("Insufficient stock for product " + product.getName(), 400, null);
            }
        }

        InvoiceClass invoice = new InvoiceClass();
        invoice.setTenantId(dto.getTenantId());
        invoice.setName(dto.getName());
        invoice.setEmail(dto.getEmail());
        invoice.setPhone(dto.getPhone());

        try {
            invoice.setDate(LocalDate.parse(dto.getDate()));
        } catch (Exception e) {
            return new ApiResponse<>("Invalid date format, expected yyyy-MM-dd", 400, null);
        }

        List<InvoiceItemsClass> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (InvoiceItemReqDTO itemDTO : dto.getItems()) {
            ProductClass product = productClassRepo.findById(itemDTO.getProductId()).get();

            // Generate UUID for invoice item
            String invoiceItemId = java.util.UUID.randomUUID().toString();
            
            // Use selling price from product
            BigDecimal unitPrice = product.getSellingPrice();
            
            InvoiceItemsClass item = new InvoiceItemsClass(
                invoiceItemId,
                dto.getTenantId(),
                invoice,
                product,
                itemDTO.getQuantity(),
                unitPrice
            );

            items.add(item);

            totalAmount = totalAmount.add(
                    unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()))
            );
        }

        // Save invoice first to get the invoice ID
        invoice.setItems(items);
        invoice.setTotalAmount(totalAmount);
        invoice.setRemainingAmount(totalAmount);
        InvoiceClass savedInvoice = invoiceClassRepo.save(invoice);
        
        // Deduct stock from inventory using new stock level service
        for (InvoiceItemReqDTO itemDTO : dto.getItems()) {
            stockLevelService.decreaseStock(dto.getTenantId(), itemDTO.getProductId(), itemDTO.getQuantity());
        }
        
        // Record stock movements for all items (OUT movements)
        stockMovementService.recordStockMovementsForInvoice(dto.getTenantId(), savedInvoice.getInvoiceId(), dto.getItems());
        
        return new ApiResponse<>("Invoice created successfully", 201, new InvoiceResDTO(savedInvoice));
    }

    public ApiResponse<?> returnAllInvoices(String tenantId) {
        List<InvoiceClass> allInvoices = invoiceClassRepo.findByTenantId(tenantId);
        List<InvoiceResDTO> allInvoicesDTO = allInvoices.stream()
                .map(InvoiceResDTO::new)
                .collect(Collectors.toList());
        return new ApiResponse<>("Invoices retrieved successfully", 200, allInvoicesDTO);
    }

    public ApiResponse<?> updateInvoiceStatus(String tenantId, long id, String status) {
        if (!invoiceClassRepo.existsByTenantIdAndInvoiceId(tenantId, id)) {
            return new ApiResponse<>("Invoice not found in this tenant", 404, null);
        }
        
        Optional<InvoiceClass> tempInvoice = invoiceClassRepo.findById(id);
        if (tempInvoice.isPresent()) {
            InvoiceClass invoice = tempInvoice.get();
            invoice.setStatus(InvoiceClass.Status.valueOf(status));
            invoiceClassRepo.save(invoice);

            return new ApiResponse<>("Invoice status updated successfully", 200, new InvoiceResDTO(invoice));
        }
        return new ApiResponse<>("Invoice not found", 404, null);
    }

    public ApiResponse<?> returnInvoicesWithStatus(String tenantId, String status) {
        List<InvoiceClass> allInvoicesWithStatus = invoiceClassRepo.findByTenantIdAndStatusString(tenantId, status);
        List<InvoiceResDTO> allInvoicesDTO = allInvoicesWithStatus.stream()
                .map(InvoiceResDTO::new)
                .collect(Collectors.toList());
        return new ApiResponse<>("Invoices with status " + status, 200, allInvoicesDTO);
    }

    public ApiResponse<?> returnInvoiceStatus(long id) {
        Optional<InvoiceClass> invoice = invoiceClassRepo.findById(id);
        if (invoice.isPresent()) {
            return new ApiResponse<>(
                    "Invoice status fetched successfully",
                    200,
                    invoice.get().getStatus()
            );
        } else {
            return new ApiResponse<>("Invoice not found", 404, null);
        }
    }

    public ApiResponse<?> createNewPayment(PaymentClassReqDTO paymentClassReqDTO) {
        // This method is deprecated - use PaymentService.createPayment instead
        return new ApiResponse<>("This method is deprecated. Please use PaymentService.createPayment", 400, null);
    }

    public ApiResponse<?> getAllPayments() {
        // This method is deprecated - use PaymentService.getAllPaymentsByTenant instead
        return new ApiResponse<>("This method is deprecated. Please use PaymentService.getAllPaymentsByTenant", 400, null);
    }

}
