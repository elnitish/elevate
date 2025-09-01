package com.elevate.fna.service;


import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.InvoiceReqDTO;
import com.elevate.fna.dto.InvoiceResDTO;
import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.entity.InvoiceItemsClass;
import com.elevate.fna.repository.InvoiceClassRepo;
import com.elevate.fna.repository.InvoiceItemClassRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceClassRepo invoiceClassRepo;
    private final InvoiceItemClassRepo invoiceItemClassRepo;

    @Autowired
    public InvoiceService(InvoiceClassRepo invoiceClassRepo,InvoiceItemClassRepo invoiceItemClassRepo) {
        this.invoiceClassRepo = invoiceClassRepo;
        this.invoiceItemClassRepo = invoiceItemClassRepo;
    }

    public ApiResponse<?> createNewInvoice(InvoiceReqDTO invoiceReqDTO) {
        System.out.println(invoiceReqDTO.toString());
        InvoiceClass newInvoice = new InvoiceClass(
                invoiceReqDTO.getCustomerName(),
                invoiceReqDTO.getCustomerEmail()

        );
        InvoiceItemsClass newInvoiceItem = new InvoiceItemsClass();
        System.out.println(newInvoice.toString());
        invoiceClassRepo.save(newInvoice);
        return new ApiResponse<>("Invoice created successfully",200, new InvoiceResDTO(
                newInvoice.getId(),
                newInvoice.getCustomerName(),
                newInvoice.getAmount(),
                newInvoice.getStatus(),
                newInvoice.getDate()
        ));
    }

    public ApiResponse<?> returnAllInvoices() {
        List<InvoiceClass> allInvoices = invoiceClassRepo.findAll();
        return new ApiResponse<>("Invoices", 200,allInvoices);
    }

    public ApiResponse<?> updateInvoiceStatus(long id, String status) {
        Optional<InvoiceClass> tempInvoice = invoiceClassRepo.findById(id);
        if(tempInvoice.isPresent()) {
            InvoiceClass invoice = tempInvoice.get();
            invoice.setStatus(status);
            invoiceClassRepo.save(invoice);
            return new ApiResponse<>("Invoice status updated successfully", 200, new InvoiceResDTO(
                    invoice.getId(),
                    invoice.getCustomerName(),
                    invoice.getAmount(),
                    invoice.getStatus(),
                    invoice.getDate()
            ));
        }
        return new ApiResponse<>("Invoice not found", 404,null);
    }

    public ApiResponse<?> returnInvoicesWithStatus(String status) {
        System.out.println(status);
        List<InvoiceClass> allInvoicesWithStatus = invoiceClassRepo.findByStatus(status);
        return new ApiResponse<>("Invoices with status "+status, 200,allInvoicesWithStatus);
    }
}
