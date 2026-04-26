package com.elevate.fna.event;

import com.elevate.crm.service.CustomerLedgerService;
import com.elevate.fna.entity.InvoiceClass;
import com.elevate.insc.service.StockMovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Async listener that processes post-invoice-creation work
 * after the transaction commits successfully.
 */
@Component
public class InvoiceEventListener {

    private static final Logger log = LoggerFactory.getLogger(InvoiceEventListener.class);

    private final StockMovementService stockMovementService;
    private final CustomerLedgerService customerLedgerService;

    @Autowired
    public InvoiceEventListener(StockMovementService stockMovementService,
                                 CustomerLedgerService customerLedgerService) {
        this.stockMovementService = stockMovementService;
        this.customerLedgerService = customerLedgerService;
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInvoiceCreated(InvoiceCreatedEvent event) {
        InvoiceClass invoice = event.getInvoice();
        log.info("Async processing for invoice #{}", invoice.getInvoiceNumber());

        try {
            // Record stock movements for each item
            for (var item : invoice.getItems()) {
                stockMovementService.recordStockMovementForInvoice(
                        invoice.getTenantId(),
                        item.getProduct().getId(),
                        String.valueOf(invoice.getInvoiceId()),
                        item.getQuantity(),
                        "Invoice: " + invoice.getInvoiceNumber());
            }

            // Record customer ledger entry
            customerLedgerService.addEntryForInvoice(invoice);

            log.info("Async processing complete for invoice #{}", invoice.getInvoiceNumber());
        } catch (Exception e) {
            log.error("Failed async processing for invoice #{}: {}", invoice.getInvoiceNumber(), e.getMessage(), e);
        }
    }
}
