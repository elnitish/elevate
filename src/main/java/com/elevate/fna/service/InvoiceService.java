package com.elevate.fna.service;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.InvoiceItemReqDTO;
import com.elevate.fna.dto.InvoiceReqDTO;
import com.elevate.fna.dto.InvoiceResDTO;
import com.elevate.fna.dto.MapperDTO;
import com.elevate.fna.dto.PaymentClassReqDTO;
import com.elevate.fna.entity.InvoiceClass;
import static com.elevate.fna.entity.InvoiceClass.Status.PAID;
import com.elevate.fna.entity.InvoiceItemsClass;
import com.elevate.fna.entity.PaymentClass;
import com.elevate.fna.repository.InvoiceClassRepo;
import com.elevate.fna.repository.PaymentClassRepo;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.repository.ProductClassRepo;
import com.elevate.insc.service.InventoryService;
import com.elevate.insc.service.StockMovementService;

@Service
public class InvoiceService {

    private final InvoiceClassRepo invoiceClassRepo;
    private final ProductClassRepo productClassRepo;
    private MapperDTO mapperDTO;
    private PaymentClassRepo paymentClassRepo;
    private InventoryService inventoryService;
    private StockMovementService stockMovementService;

    @Autowired
    public InvoiceService(InvoiceClassRepo invoiceClassRepo,
                          ProductClassRepo productClassRepo,
                          MapperDTO mapperDTO,
                          PaymentClassRepo paymentClassRepo,
                          InventoryService inventoryService,
                          StockMovementService stockMovementService) {

        this.invoiceClassRepo = invoiceClassRepo;
        this.productClassRepo = productClassRepo;
        this.mapperDTO = mapperDTO;
        this.paymentClassRepo = paymentClassRepo;
        this.inventoryService = inventoryService;
        this.stockMovementService = stockMovementService;
    }

    public ApiResponse<?> createNewInvoice(InvoiceReqDTO dto) {
        InvoiceClass invoice = new InvoiceClass();
        invoice.setName(dto.getName());
        invoice.setEmail(dto.getEmail());

        try {
            invoice.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(dto.getDate()));
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format, expected yyyy-MM-dd");
        }

        List<InvoiceItemsClass> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (InvoiceItemReqDTO itemDTO : dto.getItems()) {
            ProductClass product = productClassRepo.findById(itemDTO.getProductId()).orElse(null);

            InvoiceItemsClass item = new InvoiceItemsClass();
            item.setInvoice(invoice);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());

            items.add(item);

            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()))
            );

        }
        inventoryService.deductStock(dto.getItems());

        invoice.setItems(items);
        invoice.setTotalAmount(totalAmount);
        stockMovementService.recordStockMomentForInvoices(invoice);
        InvoiceClass response = invoiceClassRepo.save(invoice);
        return new ApiResponse<>("Invoice created successfully", 200, response);
    }

    public ApiResponse<?> returnAllInvoices() {
        List<InvoiceClass> allInvoices = invoiceClassRepo.findAll();
        System.out.println(allInvoices);
        List<InvoiceResDTO> allInvoicesDTO = mapperDTO.toInvoiceResDtoList(allInvoices);
        System.out.println(allInvoicesDTO.toString());
        return new ApiResponse<>("Invoices", 200, allInvoicesDTO);
    }

    public ApiResponse<?> updateInvoiceStatus(long id, String status) {
        Optional<InvoiceClass> tempInvoice = invoiceClassRepo.findById(id);
        if (tempInvoice.isPresent()) {
            InvoiceClass invoice = tempInvoice.get();
            invoice.setStatus(InvoiceClass.Status.valueOf(status));
            invoiceClassRepo.save(invoice);

            return new ApiResponse<>("Invoice status updated successfully", 200, new InvoiceResDTO(
                    invoice.getInvoiceId(),
                    invoice.getName(),
                    invoice.getTotalAmount(),
                    invoice.getRemainingAmount(),
                    invoice.getStatus().toString(),
                    invoice.getDate(),
                    mapperDTO.toInvoiceItemResDtoList(invoice.getItems())
            ));
        }
        return new ApiResponse<>("Invoice not found", 404, null);
    }

    public ApiResponse<?> returnInvoicesWithStatus(String status) {
        System.out.println(status);
        List<InvoiceClass> allInvoicesWithStatus = invoiceClassRepo.findByStatus(status);
        return new ApiResponse<>("Invoices with status " + status, 200, allInvoicesWithStatus);
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
        // Validate that invoiceID is not null
        if (paymentClassReqDTO.getInvoiceID() == null) {
            return new ApiResponse<>("Invoice ID is required and cannot be null", 400, null);
        }
        
        InvoiceClass invoice = invoiceClassRepo.findById(paymentClassReqDTO.getInvoiceID()).orElse(null);
        if (invoice == null) {
            return new ApiResponse<>("Invoice not found", 404, null);
        }
        PaymentClass newPayment = mapperDTO.toPaymentClass(paymentClassReqDTO);
        invoice.setRemainingAmount(invoice.getTotalAmount().subtract(paymentClassReqDTO.getTotalAmount()));
        if (invoice.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(PAID);
        }
        paymentClassRepo.save(newPayment);
        return new ApiResponse<>("Payment created successfully", 200, newPayment);

    }

    public ApiResponse<?> getAllPayments() {
        List<PaymentClass> allPayments = paymentClassRepo.findAll();
        return new ApiResponse<>("Payments", 200, allPayments);
    }

}
