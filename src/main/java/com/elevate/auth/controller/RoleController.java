package com.elevate.auth.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.RoleDTO;
import com.elevate.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createRole(@RequestBody RoleDTO roleDTO) {
        ApiResponse<?> response = roleService.createRole(roleDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllRoles() {
        ApiResponse<?> response = roleService.getAllRoles();
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getRoleById(@PathVariable Integer id) {
        ApiResponse<?> response = roleService.getRoleById(id);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateRole(@PathVariable Integer id, @RequestBody RoleDTO roleDTO) {
        ApiResponse<?> response = roleService.updateRole(id, roleDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteRole(@PathVariable Integer id) {
        ApiResponse<?> response = roleService.deleteRole(id);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}
