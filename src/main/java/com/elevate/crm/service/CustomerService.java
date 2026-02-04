package com.elevate.crm.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.elevate.crm.entity.CustomerBalanceClass;
import com.elevate.crm.repository.CustomerBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.repository.TenantRepository;
import com.elevate.crm.dto.CustomerReqDTO;
import com.elevate.crm.dto.CustomerResDTO;
import com.elevate.crm.entity.CustomerClass;
import com.elevate.crm.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TenantRepository tenantRepository;
    private final CustomerBalanceRepository customerBalanceRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, TenantRepository tenantRepository,CustomerBalanceRepository customerBalanceRepository) {
        this.customerRepository = customerRepository;
        this.tenantRepository = tenantRepository;
        this.customerBalanceRepository = customerBalanceRepository;
    }

    @Transactional
    public ApiResponse<?> createCustomer(String tenantId, CustomerReqDTO dto) {
        if (dto.getPhone() != null && customerRepository.existsByTenantIdAndPhone(tenantId, dto.getPhone())) {
            return new ApiResponse<>("Customer with this phone already exists", 409, null);
        }
        CustomerClass c  = new CustomerClass();
        c.setTenantId(tenantId);
        c.setName(dto.getName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        c.setAddress(dto.getAddress());
        c.setSource(dto.getSource());
        c.setNotes(dto.getNotes());
        CustomerClass saved = customerRepository.save(c);
        CustomerBalanceClass cb = new CustomerBalanceClass(saved);
        customerBalanceRepository.save(cb);
        return new ApiResponse<>("Customer created successfully", 201, new CustomerResDTO(saved));
    }

    public ApiResponse<?> getCustomers(String tenantId) {
        List<CustomerResDTO> list = customerRepository.findByTenantId(tenantId)
                .stream().map(CustomerResDTO::new).collect(Collectors.toList());
        return new ApiResponse<>("Customers retrieved successfully", 200, list);
    }

    public ApiResponse<?> getCustomerById(String tenantId, Long id) {
        Optional<CustomerClass> opt = customerRepository.findByTenantIdAndId(tenantId, id);
        if (opt.isEmpty()) {
            return new ApiResponse<>("Customer not found", 404, null);
        }
        return new ApiResponse<>("Customer retrieved successfully", 200, new CustomerResDTO(opt.get()));
    }

    @Transactional
    public ApiResponse<?> updateCustomer(String tenantId, Long id, CustomerReqDTO dto) {
        Optional<CustomerClass> opt = customerRepository.findByTenantIdAndId(tenantId, id);
        if (opt.isEmpty()) {
            return new ApiResponse<>("Customer not found", 404, null);
        }
        CustomerClass c = opt.get();
        if (dto.getName() != null) c.setName(dto.getName());
        if (dto.getEmail() != null) c.setEmail(dto.getEmail());
        if (dto.getPhone() != null) c.setPhone(dto.getPhone());
        if (dto.getAddress() != null) c.setAddress(dto.getAddress());
        if (dto.getSource() != null) c.setSource(dto.getSource());
        if (dto.getNotes() != null) c.setNotes(dto.getNotes());
        CustomerClass saved = customerRepository.save(c);
        return new ApiResponse<>("Customer updated successfully", 200, new CustomerResDTO(saved));
    }

    @Transactional
    public ApiResponse<?> deleteCustomer(String tenantId, Long id) {
        Optional<CustomerClass> opt = customerRepository.findByTenantIdAndId(tenantId, id);
        if (opt.isEmpty()) {
            return new ApiResponse<>("Customer not found", 404, null);
        }
        customerRepository.delete(opt.get());
        return new ApiResponse<>("Customer deleted successfully", 200, null);
    }
}


