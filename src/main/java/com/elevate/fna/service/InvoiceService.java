package com.elevate.fna.service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.elevate.crm.service.CustomerLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.InvoiceItemReqDTO;
import com.elevate.crm.entity.CustomerClass;
import com.elevate.crm.repository.CustomerRepository;
import com.elevate.fna.dto.InvoiceReqDTO;
import com.elevate.fna.dto.InvoiceResDTO;
import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.entity.InvoiceItemsClass;
import com.elevate.fna.repository.InvoiceClassRepo;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.entity.StockLevelClass;
import com.elevate.insc.service.ProductService;
import com.elevate.insc.service.StockLevelService;
import com.elevate.insc.service.StockMovementService;

@Service
public class InvoiceService {

    private final InvoiceClassRepo invoiceClassRepo;
    private final ProductService productService;
    private final StockMovementService stockMovementService;
    private final StockLevelService stockLevelService;
    private final CustomerRepository customerRepository;
    private final CustomerLedgerService customerLedgerService;

    @Autowired
    public InvoiceService(InvoiceClassRepo invoiceClassRepo,
                          ProductService productService,
                          StockMovementService stockMovementService,
                          StockLevelService stockLevelService,
                          CustomerLedgerService customerLedgerService,
                          CustomerRepository customerRepository) {

        this.invoiceClassRepo = invoiceClassRepo;
        this.productService = productService;
        this.stockMovementService = stockMovementService;
        this.stockLevelService = stockLevelService;
        this.customerLedgerService = customerLedgerService;
        this.customerRepository = customerRepository;
    }

    public ApiResponse<?> createNewInvoice(String tenantId, InvoiceReqDTO dto) {
        // Validate input
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            return new ApiResponse<>("Invoice must contain at least one item", 400, null);
        }

        InvoiceClass invoice = new InvoiceClass();
        invoice.setTenantId(tenantId);
        // Resolve and set Customer entity
        java.util.Optional<CustomerClass> customerOpt = customerRepository.findByTenantIdAndId(tenantId, dto.getCustomerId());
        if (customerOpt.isEmpty()) {
            return new ApiResponse<>("Customer not found", 404, null);
        }
        invoice.setCustomer(customerOpt.get());
        invoice.setName(dto.getName());
        invoice.setEmail(dto.getEmail());
        invoice.setPhone(dto.getPhone());
        invoice.setStatus(dto.getStatus());

        try {
            invoice.setDate(LocalDate.parse(dto.getDate()));
        } catch (Exception e) {
            return new ApiResponse<>("Invalid date format, expected yyyy-MM-dd", 400, null);
        }

        List<InvoiceItemsClass> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (InvoiceItemReqDTO itemDTO : dto.getItems()) {
            // Validate product exists and belongs to tenant
            Optional<ProductClass> productOpt = productService.getProductById(itemDTO.getProductId());
            if (productOpt.isEmpty()) {
                return new ApiResponse<>("Product not found: " + itemDTO.getProductId(), 404, null);
            }
            ProductClass product = productOpt.get();
            
            // Validate quantity
            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                return new ApiResponse<>("Invalid quantity for product: " + itemDTO.getProductId(), 400, null);
            }
            
            // Check stock availability before creating invoice
            Optional<StockLevelClass> stockLevelOpt = stockLevelService.getStockLevel(tenantId, itemDTO.getProductId());
            Integer availableStock = stockLevelOpt.map(StockLevelClass::getQuantity).orElse(0);
            if (availableStock < itemDTO.getQuantity()) {
                return new ApiResponse<>("Insufficient stock for product: " + product.getName() + 
                    ". Available: " + availableStock + 
                    ", Required: " + itemDTO.getQuantity(), 400, null);
            }

            // Generate UUID for invoice item
            String invoiceItemId = java.util.UUID.randomUUID().toString();
            
            // Use selling price from product
            BigDecimal unitPrice = product.getSellingPrice();
            
            InvoiceItemsClass item = new InvoiceItemsClass(
                invoiceItemId,
                tenantId,
                invoice,
                product,
                itemDTO.getQuantity(),
                unitPrice
            );

            items.add(item);

            totalAmount = totalAmount.add(
                    unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()))
            );
            
            // Deduct stock and record stock movement for this item
            stockLevelService.decreaseStock(tenantId, itemDTO.getProductId(), itemDTO.getQuantity());
            // defer stock movement and ledger until invoice has a persisted id
        }

        // Set the temporary invoice ID and save invoice
        invoice.setItems(items);
        invoice.setTotalAmount(totalAmount);
        if(invoice.getStatus().equals(InvoiceClass.Status.PAID)) {
            invoice.setRemainingAmount(BigDecimal.ZERO);
        } else {
            invoice.setRemainingAmount(totalAmount);
        }
        InvoiceClass savedInvoice = invoiceClassRepo.save(invoice);
        // Now we have a numeric invoiceId; record stock movements and ledger
        for (var item : savedInvoice.getItems()) {
            stockMovementService.recordStockMovementForInvoice(tenantId, item.getProduct().getId(), String.valueOf(savedInvoice.getInvoiceId()), item.getQuantity(), "Invoice: " + savedInvoice.getInvoiceId());
        }
        customerLedgerService.addEntryForInvoice(savedInvoice);
        
        return new ApiResponse<>("Invoice created successfully", 201, new InvoiceResDTO(savedInvoice));
    }

    public ApiResponse<?> returnAllInvoices(String tenantId) {
        List<InvoiceClass> allInvoices = invoiceClassRepo.findByTenantId(tenantId);
        List<InvoiceResDTO> allInvoicesDTO = allInvoices.stream()
                .map(InvoiceResDTO::new)
                .collect(Collectors.toList());
        return new ApiResponse<>("Invoices retrieved successfully", 200, allInvoicesDTO);
    }

    public ApiResponse<?> returnInvoicesWithStatus(String tenantId, String status) {
        List<InvoiceClass> allInvoicesWithStatus = invoiceClassRepo.findByTenantIdAndStatusString(tenantId, status);
        List<InvoiceResDTO> allInvoicesDTO = allInvoicesWithStatus.stream()
                .map(InvoiceResDTO::new)
                .collect(Collectors.toList());
        return new ApiResponse<>("Invoices with status " + status, 200, allInvoicesDTO);
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

    public ApiResponse<?> returnInvoiceWithID(String tenantID, Long id) {
        Optional<InvoiceClass> invoiceClass = invoiceClassRepo.findByTenantIdAndInvoiceId(tenantID, id);
        if(invoiceClass.isEmpty()){
            return new ApiResponse<>("Invoice not found", 404, null);
        }
        else{
            InvoiceClass invoice = invoiceClass.get();
            return new ApiResponse<>("Invoice returned successfully", 201, invoice);
        }
    }
}
