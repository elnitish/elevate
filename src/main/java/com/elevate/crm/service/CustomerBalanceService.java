package com.elevate.crm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.crm.dto.CustomerBalanceResDTO;
import com.elevate.crm.dto.CustomerBalanceUpdateReqDTO;
import com.elevate.crm.entity.CustomerBalanceClass;
import com.elevate.crm.entity.CustomerBalanceId;
import com.elevate.crm.entity.CustomerClass;
import com.elevate.crm.repository.CustomerBalanceRepository;
import com.elevate.crm.repository.CustomerRepository;

@Service
public class CustomerBalanceService {

    private final CustomerBalanceRepository balanceRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerBalanceService(CustomerBalanceRepository balanceRepository, CustomerRepository customerRepository) {
        this.balanceRepository = balanceRepository;
        this.customerRepository = customerRepository;
    }

    public ApiResponse<?> getAllBalances(String tenantId) {
        List<CustomerBalanceResDTO> list = balanceRepository.findByIdTenantId(tenantId)
                .stream().map(CustomerBalanceResDTO::new).collect(Collectors.toList());
        return new ApiResponse<>("Customer balances retrieved", 200, list);
    }

    public ApiResponse<?> getBalance(String tenantId, Long customerId) {
        Optional<CustomerBalanceClass> opt = balanceRepository.findByIdTenantIdAndIdCustomerId(tenantId, customerId);
        if (opt.isEmpty()) {
            return new ApiResponse<>("Customer balance not found", 404, null);
        }
        return new ApiResponse<>("Customer balance retrieved", 200, new CustomerBalanceResDTO(opt.get()));
    }

    @Transactional
    public ApiResponse<?> upsertBalance(String tenantId, CustomerBalanceUpdateReqDTO dto) {
        Optional<CustomerClass> customerOpt = customerRepository.findByTenantIdAndId(tenantId, dto.getCustomerId());
        if (customerOpt.isEmpty()) {
            return new ApiResponse<>("Customer not found", 404, null);
        }

        CustomerBalanceId id = new CustomerBalanceId(tenantId, dto.getCustomerId());
        CustomerBalanceClass entity = balanceRepository.findById(id).orElseGet(() -> {
            CustomerBalanceClass cb = new CustomerBalanceClass();
            cb.setId(id);
            cb.setCustomer(customerOpt.get());
            cb.setTotalDebit(BigDecimal.ZERO);
            cb.setTotalCredit(BigDecimal.ZERO);
            return cb;
        });

        if (dto.getTotalDebit() != null) {
            entity.setTotalDebit(dto.getTotalDebit());
        }
        if (dto.getTotalCredit() != null) {
            entity.setTotalCredit(dto.getTotalCredit());
        }

        CustomerBalanceClass saved = balanceRepository.save(entity);
        return new ApiResponse<>("Customer balance saved", 200, new CustomerBalanceResDTO(saved));
    }
}


