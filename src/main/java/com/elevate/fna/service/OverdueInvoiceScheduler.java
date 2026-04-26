package com.elevate.fna.service;

import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.repository.InvoiceClassRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduled job to detect and mark overdue invoices.
 * Runs daily at 1 AM.
 */
@Component
public class OverdueInvoiceScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueInvoiceScheduler.class);

    private final InvoiceClassRepo invoiceClassRepo;

    @Autowired
    public OverdueInvoiceScheduler(InvoiceClassRepo invoiceClassRepo) {
        this.invoiceClassRepo = invoiceClassRepo;
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void markOverdueInvoices() {
        LocalDate today = LocalDate.now();
        List<InvoiceClass.Status> eligibleStatuses = List.of(
                InvoiceClass.Status.PENDING,
                InvoiceClass.Status.PARTIALLY_PAID
        );

        List<InvoiceClass> overdueInvoices = invoiceClassRepo.findOverdueInvoices(eligibleStatuses, today);

        int count = 0;
        for (InvoiceClass invoice : overdueInvoices) {
            invoice.setStatus(InvoiceClass.Status.OVERDUE);
            invoiceClassRepo.save(invoice);
            count++;
        }

        if (count > 0) {
            log.info("Marked {} invoices as OVERDUE", count);
        }
    }
}
