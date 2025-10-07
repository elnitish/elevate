package com.elevate.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.TenantDTO;
import com.elevate.auth.dto.TenantReqDTO;
import com.elevate.auth.dto.UserClassReqDTO;
import com.elevate.auth.dto.UserCreateReqDTO;
import com.elevate.auth.service.TenantService;
import com.elevate.auth.service.UserService;

@RestController
@RequestMapping("/auth")
public class ApiAuth {

    private final UserService userService;
    private final TenantService tenantService;

    @Autowired
    public ApiAuth(UserService userService, TenantService tenantService) {
        this.userService = userService;
        this.tenantService = tenantService;
    }

    // User Authentication Services
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody UserClassReqDTO userClassReqDTO){
        ApiResponse<?> httpResponse = userService.returnUser(userClassReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerTenant(@RequestBody TenantReqDTO tenantReqDTO){
        ApiResponse<?> httpResponse = tenantService.createTenant(tenantReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    @GetMapping("/tenant/{tenantId}/users")
    public ResponseEntity<ApiResponse<?>> getUsersByTenant(@PathVariable String tenantId) {
        ApiResponse<?> httpResponse = userService.getUsersByTenant(tenantId);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<?>> createUser(@RequestBody UserCreateReqDTO userCreateReqDTO) {
        ApiResponse<?> httpResponse = userService.createUser(userCreateReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    // Session Token Services
    @GetMapping("/validate-token/{sessionToken}")
    public ResponseEntity<ApiResponse<?>> validateSessionToken(@PathVariable String sessionToken) {
        ApiResponse<?> response = userService.validateSessionToken(sessionToken);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PostMapping("/logout/{sessionToken}")
    public ResponseEntity<ApiResponse<?>> logoutUser(@PathVariable String sessionToken) {
        ApiResponse<?> response = userService.logoutUser(sessionToken);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
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
