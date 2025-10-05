package com.elevate.auth.controllers;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.UserClassReqDTO;
import com.elevate.auth.service.UserRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class ApiAuth {

    private final UserRegService userRegService;

    @Autowired
    public ApiAuth(UserRegService userRegService) {
        this.userRegService = userRegService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody UserClassReqDTO userClassReqDTO){
        ApiResponse<?> httpResponse = userRegService.registerNewUser(userClassReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody UserClassReqDTO userClassReqDTO){
        ApiResponse<?> httpResponse = userRegService.returnUser(userClassReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }
    
    @GetMapping("/user/email/{email}")
    public ResponseEntity<ApiResponse<?>> getUserByEmail(@PathVariable String email) {
        ApiResponse<?> httpResponse = userRegService.getUserByEmail(email);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }
    
    @PostMapping("/user/{userId}/roles")
    public ResponseEntity<ApiResponse<?>> assignRolesToUser(@PathVariable Long userId, @RequestBody List<String> roleNames) {
        ApiResponse<?> httpResponse = userRegService.assignRolesToUser(userId, roleNames);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }
    
    @DeleteMapping("/user/{userId}/roles")
    public ResponseEntity<ApiResponse<?>> removeRolesFromUser(@PathVariable Long userId, @RequestBody List<String> roleNames) {
        ApiResponse<?> httpResponse = userRegService.removeRolesFromUser(userId, roleNames);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }
}
