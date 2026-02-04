package com.elevate.fna.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.fna.dto.PayrollReqDTO;
import com.elevate.fna.dto.PayrollResDTO;
import com.elevate.fna.entity.PayrollClass;
import com.elevate.fna.repository.PayrollRepository;
import com.elevate.hrs.entity.EmployeeClass;
import com.elevate.hrs.repository.EmployeeClassRepo;

@Service
public class PayrollService {

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private EmployeeClassRepo employeeRepository;

    @Transactional
    public PayrollResDTO createPayroll(String tenantId, PayrollReqDTO payrollReqDTO) {
        // Validate employee exists
        Optional<EmployeeClass> employee = employeeRepository.findById(payrollReqDTO.getEmployeeId());
        if (employee.isEmpty()) {
            throw new RuntimeException("Employee not found with id: " + payrollReqDTO.getEmployeeId());
        }

        // Check if payroll already exists for this month
        Optional<PayrollClass> existingPayroll = payrollRepository.findByTenantIdAndEmployeeIdAndYearMonth(
                tenantId, payrollReqDTO.getEmployeeId(), payrollReqDTO.getYearMonth());
        if (existingPayroll.isPresent()) {
            throw new RuntimeException("Payroll already exists for this employee and month");
        }

        PayrollClass payroll = new PayrollClass();
        payroll.setTenantId(tenantId);
        payroll.setEmployee(employee.get());
        payroll.setYearMonth(payrollReqDTO.getYearMonth());
        payroll.setSalary(payrollReqDTO.getSalary());
        payroll.setBasic(payrollReqDTO.getSalary()); // Assuming basic is same as salary
        payroll.setDearnessAllowance(payrollReqDTO.getDearnessAllowance() != null ? payrollReqDTO.getDearnessAllowance() : BigDecimal.ZERO);
        payroll.setHouseRentAllowance(payrollReqDTO.getHouseRentAllowance() != null ? payrollReqDTO.getHouseRentAllowance() : BigDecimal.ZERO);
        payroll.setOtherAllowances(payrollReqDTO.getOtherAllowances() != null ? payrollReqDTO.getOtherAllowances() : BigDecimal.ZERO);
        payroll.setIncomeTax(payrollReqDTO.getIncomeTax() != null ? payrollReqDTO.getIncomeTax() : BigDecimal.ZERO);
        payroll.setProvidentFund(payrollReqDTO.getProvidentFund() != null ? payrollReqDTO.getProvidentFund() : BigDecimal.ZERO);
        payroll.setProfessionalTax(payrollReqDTO.getProfessionalTax() != null ? payrollReqDTO.getProfessionalTax() : BigDecimal.ZERO);
        payroll.setOtherDeductions(payrollReqDTO.getOtherDeductions() != null ? payrollReqDTO.getOtherDeductions() : BigDecimal.ZERO);
        payroll.setNotes(payrollReqDTO.getNotes());

        // Calculate gross and net salary
        payroll.calculateGrossSalary();
        payroll.calculateNetSalary();

        if (payrollReqDTO.getStatus() != null) {
            try {
                payroll.setStatus(PayrollClass.Status.valueOf(payrollReqDTO.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                payroll.setStatus(PayrollClass.Status.DRAFT);
            }
        } else {
            payroll.setStatus(PayrollClass.Status.DRAFT);
        }

        PayrollClass savedPayroll = payrollRepository.save(payroll);
        return convertToResDTO(savedPayroll);
    }

    public List<PayrollResDTO> getAllPayrolls(String tenantId) {
        return payrollRepository.findByTenantId(tenantId).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<PayrollResDTO> getPayrollsByStatus(String tenantId, String status) {
        return payrollRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<PayrollResDTO> getPayrollsByEmployee(String tenantId, Long employeeId) {
        return payrollRepository.findByTenantIdAndEmployeeId(tenantId, employeeId).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<PayrollResDTO> getPayrollsByMonth(String tenantId, String yearMonth) {
        return payrollRepository.findByTenantIdAndYearMonth(tenantId, yearMonth).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public Optional<PayrollResDTO> getPayrollById(String tenantId, Long payrollId) {
        Optional<PayrollClass> payroll = payrollRepository.findById(payrollId);
        if (payroll.isPresent() && payroll.get().getTenantId().equals(tenantId)) {
            return payroll.map(this::convertToResDTO);
        }
        return Optional.empty();
    }

    @Transactional
    public PayrollResDTO updatePayroll(String tenantId, Long payrollId, PayrollReqDTO payrollReqDTO) {
        Optional<PayrollClass> payroll = payrollRepository.findById(payrollId);
        if (payroll.isPresent() && payroll.get().getTenantId().equals(tenantId)) {
            PayrollClass payrollClass = payroll.get();
            
            // Only allow updates to draft payrolls
            if (!payrollClass.getStatus().equals(PayrollClass.Status.DRAFT)) {
                throw new RuntimeException("Cannot update payroll that is not in DRAFT status");
            }

            payrollClass.setSalary(payrollReqDTO.getSalary());
            payrollClass.setBasic(payrollReqDTO.getSalary());
            payrollClass.setDearnessAllowance(payrollReqDTO.getDearnessAllowance() != null ? payrollReqDTO.getDearnessAllowance() : BigDecimal.ZERO);
            payrollClass.setHouseRentAllowance(payrollReqDTO.getHouseRentAllowance() != null ? payrollReqDTO.getHouseRentAllowance() : BigDecimal.ZERO);
            payrollClass.setOtherAllowances(payrollReqDTO.getOtherAllowances() != null ? payrollReqDTO.getOtherAllowances() : BigDecimal.ZERO);
            payrollClass.setIncomeTax(payrollReqDTO.getIncomeTax() != null ? payrollReqDTO.getIncomeTax() : BigDecimal.ZERO);
            payrollClass.setProvidentFund(payrollReqDTO.getProvidentFund() != null ? payrollReqDTO.getProvidentFund() : BigDecimal.ZERO);
            payrollClass.setProfessionalTax(payrollReqDTO.getProfessionalTax() != null ? payrollReqDTO.getProfessionalTax() : BigDecimal.ZERO);
            payrollClass.setOtherDeductions(payrollReqDTO.getOtherDeductions() != null ? payrollReqDTO.getOtherDeductions() : BigDecimal.ZERO);
            payrollClass.setNotes(payrollReqDTO.getNotes());

            payrollClass.calculateGrossSalary();
            payrollClass.calculateNetSalary();

            PayrollClass updatedPayroll = payrollRepository.save(payrollClass);
            return convertToResDTO(updatedPayroll);
        }
        return null;
    }

    @Transactional
    public boolean deletePayroll(String tenantId, Long payrollId) {
        Optional<PayrollClass> payroll = payrollRepository.findById(payrollId);
        if (payroll.isPresent() && payroll.get().getTenantId().equals(tenantId)) {
            // Only allow deletion of draft payrolls
            if (!payroll.get().getStatus().equals(PayrollClass.Status.DRAFT)) {
                throw new RuntimeException("Cannot delete payroll that is not in DRAFT status");
            }
            payrollRepository.deleteById(payrollId);
            return true;
        }
        return false;
    }

    @Transactional
    public PayrollResDTO submitPayroll(String tenantId, Long payrollId) {
        Optional<PayrollClass> payroll = payrollRepository.findById(payrollId);
        if (payroll.isPresent() && payroll.get().getTenantId().equals(tenantId)) {
            PayrollClass payrollClass = payroll.get();
            if (payrollClass.getStatus().equals(PayrollClass.Status.DRAFT)) {
                payrollClass.setStatus(PayrollClass.Status.PENDING_APPROVAL);
                PayrollClass updatedPayroll = payrollRepository.save(payrollClass);
                return convertToResDTO(updatedPayroll);
            }
        }
        return null;
    }

    @Transactional
    public PayrollResDTO approvePayroll(String tenantId, Long payrollId) {
        Optional<PayrollClass> payroll = payrollRepository.findById(payrollId);
        if (payroll.isPresent() && payroll.get().getTenantId().equals(tenantId)) {
            PayrollClass payrollClass = payroll.get();
            if (payrollClass.getStatus().equals(PayrollClass.Status.PENDING_APPROVAL)) {
                payrollClass.setStatus(PayrollClass.Status.APPROVED);
                PayrollClass updatedPayroll = payrollRepository.save(payrollClass);
                return convertToResDTO(updatedPayroll);
            }
        }
        return null;
    }

    @Transactional
    public PayrollResDTO processPayroll(String tenantId, Long payrollId) {
        Optional<PayrollClass> payroll = payrollRepository.findById(payrollId);
        if (payroll.isPresent() && payroll.get().getTenantId().equals(tenantId)) {
            PayrollClass payrollClass = payroll.get();
            if (payrollClass.getStatus().equals(PayrollClass.Status.APPROVED)) {
                payrollClass.setStatus(PayrollClass.Status.PROCESSED);
                PayrollClass updatedPayroll = payrollRepository.save(payrollClass);
                return convertToResDTO(updatedPayroll);
            }
        }
        return null;
    }

    @Transactional
    public PayrollResDTO payPayroll(String tenantId, Long payrollId) {
        Optional<PayrollClass> payroll = payrollRepository.findById(payrollId);
        if (payroll.isPresent() && payroll.get().getTenantId().equals(tenantId)) {
            PayrollClass payrollClass = payroll.get();
            if (payrollClass.getStatus().equals(PayrollClass.Status.PROCESSED)) {
                payrollClass.setStatus(PayrollClass.Status.PAID);
                payrollClass.setPaymentDate(LocalDate.now());
                PayrollClass updatedPayroll = payrollRepository.save(payrollClass);
                return convertToResDTO(updatedPayroll);
            }
        }
        return null;
    }

    private PayrollResDTO convertToResDTO(PayrollClass payroll) {
        PayrollResDTO resDTO = new PayrollResDTO();
        resDTO.setPayrollId(payroll.getPayrollId());
        resDTO.setTenantId(payroll.getTenantId());
        resDTO.setEmployeeId(payroll.getEmployee().getId());
        resDTO.setEmployeeName(payroll.getEmployee().getName());
        resDTO.setYearMonth(payroll.getYearMonth());
        resDTO.setSalary(payroll.getSalary());
        resDTO.setGrossSalary(payroll.getGrossSalary());
        resDTO.setBasic(payroll.getBasic());
        resDTO.setDearnessAllowance(payroll.getDearnessAllowance());
        resDTO.setHouseRentAllowance(payroll.getHouseRentAllowance());
        resDTO.setOtherAllowances(payroll.getOtherAllowances());
        resDTO.setIncomeTax(payroll.getIncomeTax());
        resDTO.setProvidentFund(payroll.getProvidentFund());
        resDTO.setProfessionalTax(payroll.getProfessionalTax());
        resDTO.setOtherDeductions(payroll.getOtherDeductions());
        resDTO.setNetSalary(payroll.getNetSalary());
        resDTO.setStatus(payroll.getStatus().toString());
        resDTO.setPaymentDate(payroll.getPaymentDate());
        resDTO.setNotes(payroll.getNotes());
        resDTO.setCreatedAt(payroll.getCreatedAt());
        resDTO.setUpdatedAt(payroll.getUpdatedAt());
        return resDTO;
    }
}
