package com.elevate.fna.event;

import com.elevate.fna.entity.InvoiceClass;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published after an invoice is successfully persisted.
 * Async listeners handle stock movement recording and ledger entries.
 */
@Getter
public class InvoiceCreatedEvent extends ApplicationEvent {

    private final InvoiceClass invoice;

    public InvoiceCreatedEvent(Object source, InvoiceClass invoice) {
        super(source);
        this.invoice = invoice;
    }
}
