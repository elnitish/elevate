package com.elevate.auth.controllers;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.UserClassReqDTO;
import com.elevate.auth.service.UserRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAuth {

    private final UserRegService userRegService;

    @Autowired
    public ApiAuth(UserRegService userRegService) {
        this.userRegService = userRegService;
    }

    @PostMapping("/addUser")
    public ResponseEntity<ApiResponse<?>> addUser(@RequestBody UserClassReqDTO userClassReqDTO){
        ApiResponse<?> httpResponse= userRegService.registerNewUser(userClassReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    @GetMapping("/getUser")
    public ResponseEntity<ApiResponse<?>> getUser(@RequestBody UserClassReqDTO userClassReqDTO){
        ApiResponse<?> httpResponse = userRegService.returnUser(userClassReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }
}
