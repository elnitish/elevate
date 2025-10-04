package com.elevate.insc.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.dto.UpdatePurchaseOrderStatusReqDTO;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.entity.PurchaseOrderClass;
import com.elevate.insc.entity.SupplierClass;
import com.elevate.insc.service.InventoryService;
import com.elevate.insc.service.PurchaseOrderService;
import com.elevate.insc.service.StockMovementService;
import com.elevate.insc.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class INSC_EndPoints {

    private final InventoryService inventoryService;
    private final SupplierService supplierService;
    private final PurchaseOrderService orderService;
    private final StockMovementService stockMovementService;

    @Autowired
    public INSC_EndPoints(InventoryService inventoryService, SupplierService supplierService, PurchaseOrderService orderService, StockMovementService stockMovementService) {
        this.inventoryService = inventoryService;
        this.supplierService = supplierService;
        this.orderService = orderService;
        this.stockMovementService = stockMovementService;
    }

    @PostMapping("/insc/createProduct")
    public ResponseEntity<?> createProduct(@Valid @RequestBody List<ProductClass> product) {
        ApiResponse<?> response = inventoryService.createNewProduct(product);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping("/insc/getAllProducts")
    public ResponseEntity<?> getAllProducts() {
        ApiResponse<?> response = inventoryService.returnAllProducts();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }


    @GetMapping("/insc/getAllProductStock")
    public ResponseEntity<?> getAllStock() {
        ApiResponse<?> response = inventoryService.returnAllProductStock();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @PostMapping("/insc/addSupplier")
    public ResponseEntity<?> addSupplier(@RequestBody SupplierClass supplier) {
        ApiResponse<?> response = supplierService.addSupplier(supplier);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // GET /inventory/suppliers
    @GetMapping("/insc/getAllSupplier")
    public ResponseEntity<?> listSuppliers() {
        ApiResponse<?> response =  supplierService.listSuppliers();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @PostMapping("/insc/createPurchaseOrder")
    public ResponseEntity<?> createOrder(@RequestBody PurchaseOrderClass order) {
        ApiResponse<?> response = orderService.createOrder(order);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // PUT /inventory/orders/{id}/received
    @PutMapping("/insc/updateStatus")
    public ResponseEntity<?> markReceived(@RequestBody UpdatePurchaseOrderStatusReqDTO updatePurchaseOrderStatusReqDTO) {
        ApiResponse<?> response = orderService.markReceived(updatePurchaseOrderStatusReqDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // GET /inventory/orders
    @GetMapping("/insc/getAllPurchaseOrder")
    public ResponseEntity<?> listOrders() {
        ApiResponse<?> response = orderService.listOrders();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }


    @GetMapping("/insc/getAllStockMovements")
    public ResponseEntity<?> returnAllStockMovements() {
        ApiResponse<?> response = stockMovementService.returnAllStockMovements();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

}
