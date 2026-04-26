package com.elevate.fna.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgingReportDTO {

    private BigDecimal currentAmount;      // not yet due
    private BigDecimal days1to30;
    private BigDecimal days31to60;
    private BigDecimal days61to90;
    private BigDecimal over90Days;
    private BigDecimal totalOutstanding;
    private List<CustomerAgingDTO> customerBreakdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerAgingDTO {
        private Long customerId;
        private String customerName;
        private BigDecimal currentAmount;
        private BigDecimal days1to30;
        private BigDecimal days31to60;
        private BigDecimal days61to90;
        private BigDecimal over90Days;
        private BigDecimal total;
    }
}
