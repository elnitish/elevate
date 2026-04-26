package com.elevate.auth.controllers;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.entity.AuthCredentials;
import com.elevate.auth.entity.TenantClass;
import com.elevate.auth.entity.UserClass;
import com.elevate.auth.repository.AuthCredentialsRepository;
import com.elevate.auth.repository.TenantRepository;
import com.elevate.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Debug controller to check database contents
 * REMOVE THIS IN PRODUCTION!
 */
@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthCredentialsRepository authCredentialsRepository;

    @GetMapping("/tenants")
    public ResponseEntity<ApiResponse<?>> getAllTenants() {
        List<TenantClass> tenants = tenantRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", tenants.size());
        response.put("tenants", tenants);
        return new ResponseEntity<>(new ApiResponse<>("Tenants retrieved", 200, response), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/tenants/{tenantId}/users")
    public ResponseEntity<ApiResponse<?>> getUsersByTenant(@PathVariable String tenantId) {
        List<UserClass> users = userRepository.findByTenantId(tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("count", users.size());
        response.put("users", users);
        return new ResponseEntity<>(new ApiResponse<>("Users retrieved", 200, response), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/tenants/{tenantId}/credentials")
    public ResponseEntity<ApiResponse<?>> getCredentialsByTenant(@PathVariable String tenantId) {
        List<AuthCredentials> allCreds = authCredentialsRepository.findAll();
        List<AuthCredentials> tenantCreds = allCreds.stream()
                .filter(c -> c.getTenantId().equals(tenantId))
                .toList();

        // Create safe response without password hashes
        List<Map<String, String>> safeCreds = tenantCreds.stream()
                .map(c -> {
                    Map<String, String> safe = new HashMap<>();
                    safe.put("tenantId", c.getTenantId());
                    safe.put("username", c.getUsername());
                    safe.put("passwordHashPrefix",
                            c.getPasswordHash().substring(0, Math.min(20, c.getPasswordHash().length())) + "...");
                    return safe;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("count", safeCreds.size());
        response.put("credentials", safeCreds);
        return new ResponseEntity<>(new ApiResponse<>("Credentials retrieved", 200, response),
                HttpStatusCode.valueOf(200));
    }

    @GetMapping("/check-tenant/{tenantId}")
    public ResponseEntity<ApiResponse<?>> checkTenant(@PathVariable String tenantId) {
        Optional<TenantClass> tenant = tenantRepository.findById(tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", tenant.isPresent());
        if (tenant.isPresent()) {
            response.put("tenant", tenant.get());
        }
        return new ResponseEntity<>(new ApiResponse<>("Tenant check", 200, response), HttpStatusCode.valueOf(200));
    }
}
