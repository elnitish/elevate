package com.elevate.fna.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.repository.TenantRepository;
import com.elevate.fna.dto.InvoiceItemReqDTO;
import com.elevate.fna.dto.InvoiceItemResDTO;
import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.entity.InvoiceItemsClass;
import com.elevate.fna.repository.InvoiceClassRepo;
import com.elevate.fna.repository.InvoiceItemsRepository;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.repository.ProductClassRepo;

@Service
public class InvoiceItemsService {
    
    private final InvoiceItemsRepository invoiceItemsRepository;
    private final InvoiceClassRepo invoiceClassRepo;
    private final ProductClassRepo productClassRepo;
    private final TenantRepository tenantRepository;
    
    @Autowired
    public InvoiceItemsService(InvoiceItemsRepository invoiceItemsRepository, 
                              InvoiceClassRepo invoiceClassRepo,
                              ProductClassRepo productClassRepo,
                              TenantRepository tenantRepository) {
        this.invoiceItemsRepository = invoiceItemsRepository;
        this.invoiceClassRepo = invoiceClassRepo;
        this.productClassRepo = productClassRepo;
        this.tenantRepository = tenantRepository;
    }
    
    @Transactional
    public ApiResponse<?> createInvoiceItem(InvoiceItemReqDTO invoiceItemReqDTO) {
        // Validate tenant exists
        if (!tenantRepository.existsById(invoiceItemReqDTO.getTenantId())) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        // Validate invoice exists and belongs to tenant
        Optional<InvoiceClass> invoiceOpt = invoiceClassRepo.findById(invoiceItemReqDTO.getInvoiceId());
        if (invoiceOpt.isEmpty()) {
            return new ApiResponse<>("Invoice not found", 404, null);
        }
        
        InvoiceClass invoice = invoiceOpt.get();
        if (!invoice.getTenantId().equals(invoiceItemReqDTO.getTenantId())) {
            return new ApiResponse<>("Invoice does not belong to this tenant", 403, null);
        }
        
        // Validate product exists and belongs to tenant
        Optional<ProductClass> productOpt = productClassRepo.findById(invoiceItemReqDTO.getProductId());
        if (productOpt.isEmpty()) {
            return new ApiResponse<>("Product not found", 404, null);
        }
        
        ProductClass product = productOpt.get();
        if (!product.getTenantId().equals(invoiceItemReqDTO.getTenantId())) {
            return new ApiResponse<>("Product does not belong to this tenant", 403, null);
        }
        
        // Generate UUID for invoice item
        String invoiceItemId = UUID.randomUUID().toString();
        
        // Create invoice item entity
        InvoiceItemsClass newInvoiceItem = new InvoiceItemsClass(
            invoiceItemId,
            invoiceItemReqDTO.getTenantId(),
            invoice,
            product,
            invoiceItemReqDTO.getQuantity(),
            invoiceItemReqDTO.getUnitPrice()
        );
        
        InvoiceItemsClass savedInvoiceItem = invoiceItemsRepository.save(newInvoiceItem);
        InvoiceItemResDTO responseDTO = new InvoiceItemResDTO(savedInvoiceItem);
        
        return new ApiResponse<>("Invoice item created successfully", 201, responseDTO);
    }
    
    public ApiResponse<?> getInvoiceItemsByInvoice(String tenantId, Long invoiceId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        List<InvoiceItemsClass> invoiceItems = invoiceItemsRepository.findByTenantIdAndInvoiceInvoiceId(tenantId, invoiceId);
        List<InvoiceItemResDTO> invoiceItemDTOs = invoiceItems.stream()
                .map(InvoiceItemResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Invoice items retrieved successfully", 200, invoiceItemDTOs);
    }
    
    public ApiResponse<?> getInvoiceItemsByProduct(String tenantId, String productId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        List<InvoiceItemsClass> invoiceItems = invoiceItemsRepository.findByTenantIdAndProductId(tenantId, productId);
        List<InvoiceItemResDTO> invoiceItemDTOs = invoiceItems.stream()
                .map(InvoiceItemResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Invoice items retrieved successfully", 200, invoiceItemDTOs);
    }
    
    public ApiResponse<?> getInvoiceItemById(String tenantId, String invoiceItemId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        Optional<InvoiceItemsClass> invoiceItemOpt = invoiceItemsRepository.findByTenantIdAndId(tenantId, invoiceItemId);
        if (invoiceItemOpt.isEmpty()) {
            return new ApiResponse<>("Invoice item not found", 404, null);
        }
        
        InvoiceItemResDTO responseDTO = new InvoiceItemResDTO(invoiceItemOpt.get());
        return new ApiResponse<>("Invoice item retrieved successfully", 200, responseDTO);
    }
    
    @Transactional
    public ApiResponse<?> updateInvoiceItem(String tenantId, String invoiceItemId, InvoiceItemReqDTO invoiceItemReqDTO) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        Optional<InvoiceItemsClass> invoiceItemOpt = invoiceItemsRepository.findByTenantIdAndId(tenantId, invoiceItemId);
        if (invoiceItemOpt.isEmpty()) {
            return new ApiResponse<>("Invoice item not found", 404, null);
        }
        
        InvoiceItemsClass invoiceItem = invoiceItemOpt.get();
        
        // Update fields
        invoiceItem.setQuantity(invoiceItemReqDTO.getQuantity());
        invoiceItem.setUnitPrice(invoiceItemReqDTO.getUnitPrice());
        invoiceItem.setLineTotal(invoiceItemReqDTO.getUnitPrice().multiply(BigDecimal.valueOf(invoiceItemReqDTO.getQuantity())));
        
        InvoiceItemsClass updatedInvoiceItem = invoiceItemsRepository.save(invoiceItem);
        InvoiceItemResDTO responseDTO = new InvoiceItemResDTO(updatedInvoiceItem);
        
        return new ApiResponse<>("Invoice item updated successfully", 200, responseDTO);
    }
    
    @Transactional
    public ApiResponse<?> deleteInvoiceItem(String tenantId, String invoiceItemId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        Optional<InvoiceItemsClass> invoiceItemOpt = invoiceItemsRepository.findByTenantIdAndId(tenantId, invoiceItemId);
        if (invoiceItemOpt.isEmpty()) {
            return new ApiResponse<>("Invoice item not found", 404, null);
        }
        
        invoiceItemsRepository.deleteById(invoiceItemId);
        return new ApiResponse<>("Invoice item deleted successfully", 200, null);
    }
    
    public ApiResponse<?> getAllInvoiceItemsByTenant(String tenantId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        List<InvoiceItemsClass> invoiceItems = invoiceItemsRepository.findByTenantId(tenantId);
        List<InvoiceItemResDTO> invoiceItemDTOs = invoiceItems.stream()
                .map(InvoiceItemResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Invoice items retrieved successfully", 200, invoiceItemDTOs);
    }
}
