package com.elevate.fna;


import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.InvoiceReqDTO;
import com.elevate.fna.dto.InvoiceResDTO;
import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.repository.InvoiceClassRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceClassRepo invoiceClassRepo;

    @Autowired
    public InvoiceService(InvoiceClassRepo invoiceClassRepo) {
        this.invoiceClassRepo = invoiceClassRepo;
    }

    public ApiResponse<?> createNewInvoice(InvoiceReqDTO invoiceReqDTO) {
        System.out.println(invoiceReqDTO.toString());
        InvoiceClass newInvoice = new InvoiceClass(
                invoiceReqDTO.getCustomerName(),
                invoiceReqDTO.getCustomerEmail(),
                invoiceReqDTO.getAmount()
        );
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
}
