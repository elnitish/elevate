package com.elevate.fna.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.entity.CustomerClass;
import com.elevate.fna.repository.CustomerClassRepo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerClassRepo customerClassRepo;
    public CustomerService(CustomerClassRepo customerClassRepo) {
        this.customerClassRepo = customerClassRepo;
    }

    public ApiResponse<?> createNewCustomer(CustomerClass customerClass) {
        customerClassRepo.save(customerClass);
        return new ApiResponse<>("New customer created successfully",200,null);

    }

    public ApiResponse<?> returnAllCustomers() {
        List<CustomerClass> customerClassList = customerClassRepo.findAll(Sort.by(Sort.Direction.ASC, "name"));
        return new ApiResponse<>("All customers returned successfully",200,customerClassList);
    }
}
