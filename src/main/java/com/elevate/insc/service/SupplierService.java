package com.elevate.insc.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.entity.SupplierClass;
import com.elevate.insc.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {
    private final SupplierRepository supplierClassRepo;


    public SupplierService(SupplierRepository supplierClassRepo) {
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
