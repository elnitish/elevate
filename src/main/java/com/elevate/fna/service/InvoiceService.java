package com.elevate.fna.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.elevate.crm.service.CustomerLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.elevate.pricing.service.PricingService;
import com.elevate.fna.event.InvoiceCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;

@Service
public class InvoiceService {

    private final InvoiceClassRepo invoiceClassRepo;
    private final ProductService productService;
    private final StockMovementService stockMovementService;
    private final StockLevelService stockLevelService;
    private final CustomerRepository customerRepository;
    private final CustomerLedgerService customerLedgerService;
    private final PricingService pricingService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public InvoiceService(InvoiceClassRepo invoiceClassRepo,
                          ProductService productService,
                          StockMovementService stockMovementService,
                          StockLevelService stockLevelService,
                          CustomerLedgerService customerLedgerService,
                          CustomerRepository customerRepository,
                          PricingService pricingService,
                          ApplicationEventPublisher eventPublisher) {
        this.invoiceClassRepo = invoiceClassRepo;
        this.productService = productService;
        this.stockMovementService = stockMovementService;
        this.stockLevelService = stockLevelService;
        this.customerLedgerService = customerLedgerService;
        this.customerRepository = customerRepository;
        this.pricingService = pricingService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ApiResponse<?> createNewInvoice(String tenantId, InvoiceReqDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            return new ApiResponse<>("Invoice must contain at least one item", 400, null);
        }

        // Resolve customer
        Optional<CustomerClass> customerOpt = customerRepository.findByTenantIdAndId(tenantId, dto.getCustomerId());
        if (customerOpt.isEmpty()) {
            return new ApiResponse<>("Customer not found", 404, null);
        }
        CustomerClass customer = customerOpt.get();

        InvoiceClass invoice = new InvoiceClass();
        invoice.setTenantId(tenantId);
        invoice.setCustomer(customer);
        invoice.setName(dto.getName());
        invoice.setEmail(dto.getEmail());
        invoice.setPhone(dto.getPhone());
        invoice.setStatus(dto.getStatus());
        invoice.setNotes(dto.getNotes());

        // Parse date
        try {
            invoice.setDate(LocalDate.parse(dto.getDate()));
        } catch (Exception e) {
            return new ApiResponse<>("Invalid date format, expected yyyy-MM-dd", 400, null);
        }

        // Payment terms: use request value, or inherit from customer
        int paymentTerms = 0;
        if (dto.getPaymentTermsDays() != null && dto.getPaymentTermsDays() > 0) {
            paymentTerms = dto.getPaymentTermsDays();
        } else if (customer.getPaymentTermsDays() != null && customer.getPaymentTermsDays() > 0) {
            paymentTerms = customer.getPaymentTermsDays();
        }
        invoice.setPaymentTermsDays(paymentTerms);

        // Calculate due date
        if (paymentTerms > 0) {
            invoice.setDueDate(invoice.getDate().plusDays(paymentTerms));
        }

        // Tax rate from request (invoice-level)
        BigDecimal taxRate = dto.getTaxRate() != null ? dto.getTaxRate() : BigDecimal.ZERO;
        invoice.setTaxRate(taxRate);

        // Invoice-level discount
        BigDecimal invoiceDiscount = dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO;

        // Build items
        List<InvoiceItemsClass> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (InvoiceItemReqDTO itemDTO : dto.getItems()) {
            Optional<ProductClass> productOpt = productService.getProductById(itemDTO.getProductId());
            if (productOpt.isEmpty()) {
                return new ApiResponse<>("Product not found: " + itemDTO.getProductId(), 404, null);
            }
            ProductClass product = productOpt.get();

            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                return new ApiResponse<>("Invalid quantity for product: " + itemDTO.getProductId(), 400, null);
            }

            // Check stock
            Optional<StockLevelClass> stockLevelOpt = stockLevelService.getStockLevel(tenantId, itemDTO.getProductId());
            Integer availableStock = stockLevelOpt.map(StockLevelClass::getQuantity).orElse(0);
            if (availableStock < itemDTO.getQuantity()) {
                return new ApiResponse<>("Insufficient stock for product: " + product.getName() +
                        ". Available: " + availableStock + ", Required: " + itemDTO.getQuantity(), 400, null);
            }

            // Resolve price via pricing engine
            BigDecimal unitPrice = pricingService.resolvePriceValue(tenantId, product.getId(), dto.getCustomerId(), itemDTO.getQuantity());

            String invoiceItemId = java.util.UUID.randomUUID().toString();
            InvoiceItemsClass item = new InvoiceItemsClass(
                    invoiceItemId, tenantId, invoice, product, itemDTO.getQuantity(), unitPrice);

            // Calculate per-item tax (same rate as invoice-level)
            item.calculateTotals(BigDecimal.ZERO, taxRate);

            items.add(item);
            subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            // Deduct stock
            stockLevelService.decreaseStock(tenantId, itemDTO.getProductId(), itemDTO.getQuantity());
        }

        // Calculate invoice totals: subtotal → discount → tax → total
        invoice.setSubtotal(subtotal);
        invoice.setDiscountAmount(invoiceDiscount);

        BigDecimal taxableAmount = subtotal.subtract(invoiceDiscount);
        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        BigDecimal taxAmount = taxableAmount.multiply(taxRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        invoice.setTaxAmount(taxAmount);

        BigDecimal totalAmount = taxableAmount.add(taxAmount);
        invoice.setTotalAmount(totalAmount);

        if (invoice.getStatus() == InvoiceClass.Status.PAID) {
            invoice.setRemainingAmount(BigDecimal.ZERO);
        } else {
            invoice.setRemainingAmount(totalAmount);
        }

        invoice.setItems(items);

        // Generate invoice number
        invoice.setInvoiceNumber(generateInvoiceNumber(tenantId, invoice.getDate()));

        InvoiceClass savedInvoice = invoiceClassRepo.save(invoice);

        // Publish event — stock movements and ledger entries handled async after commit
        eventPublisher.publishEvent(new InvoiceCreatedEvent(this, savedInvoice));

        return new ApiResponse<>("Invoice created successfully", 201, new InvoiceResDTO(savedInvoice));
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> returnAllInvoices(String tenantId) {
        List<InvoiceClass> allInvoices = invoiceClassRepo.findByTenantId(tenantId);
        List<InvoiceResDTO> allInvoicesDTO = allInvoices.stream()
                .map(InvoiceResDTO::new)
                .collect(Collectors.toList());
        return new ApiResponse<>("Invoices retrieved successfully", 200, allInvoicesDTO);
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> returnAllInvoicesPaged(String tenantId, Pageable pageable) {
        Page<InvoiceClass> invoices = invoiceClassRepo.findByTenantId(tenantId, pageable);
        Page<InvoiceResDTO> dtos = invoices.map(InvoiceResDTO::new);
        return new ApiResponse<>("Invoices retrieved successfully", 200, dtos);
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> returnInvoicesWithStatus(String tenantId, String status) {
        List<InvoiceClass> allInvoicesWithStatus = invoiceClassRepo.findByTenantIdAndStatusString(tenantId, status);
        List<InvoiceResDTO> allInvoicesDTO = allInvoicesWithStatus.stream()
                .map(InvoiceResDTO::new)
                .collect(Collectors.toList());
        return new ApiResponse<>("Invoices with status " + status, 200, allInvoicesDTO);
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> returnOverdueInvoices(String tenantId) {
        List<InvoiceClass> overdue = invoiceClassRepo.findByTenantIdAndStatus(tenantId, InvoiceClass.Status.OVERDUE);
        List<InvoiceResDTO> dtos = overdue.stream().map(InvoiceResDTO::new).collect(Collectors.toList());
        return new ApiResponse<>("Overdue invoices retrieved", 200, dtos);
    }

    @Transactional
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

    @Transactional
    public ApiResponse<?> cancelInvoice(String tenantId, long id) {
        Optional<InvoiceClass> invoiceOpt = invoiceClassRepo.findByTenantIdAndInvoiceId(tenantId, id);
        if (invoiceOpt.isEmpty()) {
            return new ApiResponse<>("Invoice not found", 404, null);
        }
        InvoiceClass invoice = invoiceOpt.get();
        if (invoice.getStatus() == InvoiceClass.Status.PAID) {
            return new ApiResponse<>("Cannot cancel a fully paid invoice", 400, null);
        }
        invoice.setStatus(InvoiceClass.Status.CANCELLED);
        invoiceClassRepo.save(invoice);
        return new ApiResponse<>("Invoice cancelled", 200, new InvoiceResDTO(invoice));
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> returnInvoiceWithID(String tenantID, Long id) {
        Optional<InvoiceClass> invoiceClass = invoiceClassRepo.findByTenantIdAndInvoiceId(tenantID, id);
        if (invoiceClass.isEmpty()) {
            return new ApiResponse<>("Invoice not found", 404, null);
        }
        return new ApiResponse<>("Invoice returned successfully", 200, new InvoiceResDTO(invoiceClass.get()));
    }

    private String generateInvoiceNumber(String tenantId, LocalDate date) {
        String prefix = "INV-" + date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        // Use count-based approach for simplicity
        long count = invoiceClassRepo.findByTenantId(tenantId).size() + 1;
        return prefix + String.format("%04d", count);
    }
}
