package com.elevate.auth.controllers;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.TenantDTO;
import com.elevate.auth.dto.UserClassReqDTO;
import com.elevate.auth.service.TenantService;
import com.elevate.auth.service.UserRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class ApiAuth {

    private final UserRegService userRegService;
    private final TenantService tenantService;

    @Autowired
    public ApiAuth(UserRegService userRegService, TenantService tenantService) {
        this.userRegService = userRegService;
        this.tenantService = tenantService;
    }

    // User Authentication Services
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody UserClassReqDTO userClassReqDTO){
        ApiResponse<?> httpResponse = userRegService.returnUser(userClassReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/users")
    public ResponseEntity<ApiResponse<?>> getUsersByTenant(@PathVariable String tenantId) {
        ApiResponse<?> httpResponse = userRegService.getUsersByTenant(tenantId);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    // Tenant Management Services
    @GetMapping("/tenants/{id}")
    public ResponseEntity<ApiResponse<?>> getTenantById(@PathVariable String id) {
        ApiResponse<?> response = tenantService.getTenantById(id);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PutMapping("/tenants/{id}")
    public ResponseEntity<ApiResponse<?>> updateTenant(@PathVariable String id, @RequestBody TenantDTO tenantDTO) {
        ApiResponse<?> response = tenantService.updateTenant(id, tenantDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}
