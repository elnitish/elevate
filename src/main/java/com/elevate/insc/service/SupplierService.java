package com.elevate.insc.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.entity.SupplierClass;
import com.elevate.insc.repository.SupplierClassRepo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupplierService {
    private final SupplierClassRepo supplierClassRepo;


    public SupplierService(SupplierClassRepo supplierClassRepo) {
        this.supplierClassRepo = supplierClassRepo;
    }

    public ApiResponse<?> addSupplier(SupplierClass supplier) {
        supplierClassRepo.save(supplier);
        return new ApiResponse<>("Supplier Added successfully",200,null);
    }

    public ApiResponse<?> listSuppliers() {
        List<SupplierClass> suppliers= supplierClassRepo.findAll();
        return new ApiResponse<>("Supplier List fetched Successfully",200,suppliers);
    }


}
