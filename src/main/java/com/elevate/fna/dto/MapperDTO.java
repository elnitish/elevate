package com.elevate.fna.dto;

import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.entity.InvoiceItemsClass;
import com.elevate.fna.entity.PaymentClass;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
//
import java.util.List;

@Component
public interface MapperDTO {

        // Invoice mappings
        InvoiceResDTO toInvoiceResDto(InvoiceClass invoiceClass);
        List<InvoiceResDTO> toInvoiceResDtoList(List<InvoiceClass> invoiceClassList);

        // Invoice Item mappings
        InvoiceItemResDTO toInvoiceItemResDto(InvoiceItemsClass invoiceItemsClass);
        List<InvoiceItemResDTO> toInvoiceItemResDtoList(List<InvoiceItemsClass> invoiceItemsClassList);

        //PaymentClass Mappings
        PaymentClass toPaymentClass(PaymentClassReqDTO paymentClassReqDTO);
}
