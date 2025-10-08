package com.elevate.auth.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.TenantReqDTO;
import com.elevate.auth.dto.UserClassReqDTO;
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
    @PostMapping("/tenantRegister")//working
    public ResponseEntity<ApiResponse<?>> registerTenant(@RequestBody TenantReqDTO tenantReqDTO){
        ApiResponse<?> httpResponse = tenantService.createTenant(tenantReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }
    @PostMapping("/createUser")//working
    public ResponseEntity<ApiResponse<?>> createUser(@RequestBody UserClassReqDTO userClassReqDTO) {
        ApiResponse<?> httpResponse = userService.createUser(userClassReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    @PostMapping("/userLogin")//working
    public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody UserClassReqDTO userClassReqDTO){
        ApiResponse<?> httpResponse = userService.loginUser(userClassReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }


    @GetMapping("/allUsers") //working
    public ResponseEntity<ApiResponse<?>> getUsersByTenant(HttpServletRequest httpRequest) {
        ApiResponse<?> httpResponse = userService.returnAllUsersByTenant((String) httpRequest.getAttribute("tenantID"));
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    @PostMapping("/userLogout")//working
    public ResponseEntity<ApiResponse<?>> logoutUser(HttpServletRequest httpRequest) {
        ApiResponse<?> response = userService.logoutUser((String) httpRequest.getAttribute("sessionKey"));
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

}