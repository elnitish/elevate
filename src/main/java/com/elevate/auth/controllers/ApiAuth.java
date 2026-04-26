package com.elevate.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import com.elevate.auth.dto.TenantReqDTO;
import com.elevate.auth.dto.UserClassReqDTO;
import com.elevate.auth.service.TenantService;
import com.elevate.auth.service.UserService;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and tenant management APIs")
public class ApiAuth {

    private final UserService userService;
    private final TenantService tenantService;

    @Autowired
    public ApiAuth(UserService userService, TenantService tenantService) {
        this.userService = userService;
        this.tenantService = tenantService;
    }

    @PostMapping("/tenantRegister")
    @Operation(summary = "Register New Tenant", description = "Create a new tenant organization in the system. This is the first step for onboarding a new business.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tenant registered successfully", content = @Content(schema = @Schema(implementation = com.elevate.auth.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Tenant already exists")
    })
    public ResponseEntity<com.elevate.auth.dto.ApiResponse<?>> registerTenant(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Tenant registration details", required = true) @RequestBody TenantReqDTO tenantReqDTO) {
        com.elevate.auth.dto.ApiResponse<?> httpResponse = tenantService.createTenant(tenantReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    @PostMapping("/createUser")
    @Operation(summary = "Create New User", description = "Create a new user within a tenant organization. Requires tenant ID and user credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = com.elevate.auth.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Username already exists in tenant")
    })
    public ResponseEntity<com.elevate.auth.dto.ApiResponse<?>> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User creation details including tenant ID, username, email, role, and password", required = true) @RequestBody UserClassReqDTO userClassReqDTO) {
        com.elevate.auth.dto.ApiResponse<?> httpResponse = userService.createUser(userClassReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    @PostMapping("/userLogin")
    @Operation(summary = "User Login", description = "Authenticate user and receive a session token. Use the session token in subsequent requests via 'Session-Key' header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful - returns user details and session token", content = @Content(schema = @Schema(implementation = com.elevate.auth.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<com.elevate.auth.dto.ApiResponse<?>> loginUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login credentials (tenantId, username, password)", required = true) @RequestBody UserClassReqDTO userClassReqDTO) {
        com.elevate.auth.dto.ApiResponse<?> httpResponse = userService.loginUser(userClassReqDTO);
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    @GetMapping("/allUsers")
    @Operation(summary = "Get All Users", description = "Retrieve all users belonging to the authenticated tenant. Requires valid session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully", content = @Content(schema = @Schema(implementation = com.elevate.auth.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing session"),
            @ApiResponse(responseCode = "404", description = "No users found")
    })
    public ResponseEntity<com.elevate.auth.dto.ApiResponse<?>> getUsersByTenant(
            @Parameter(hidden = true) HttpServletRequest httpRequest) {
        com.elevate.auth.dto.ApiResponse<?> httpResponse = userService
                .returnAllUsersByTenant((String) httpRequest.getAttribute("tenantID"));
        return new ResponseEntity<>(httpResponse, HttpStatusCode.valueOf(httpResponse.getCode()));
    }

    @PostMapping("/userLogout")
    @Operation(summary = "User Logout", description = "Invalidate the current session token and log out the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(schema = @Schema(implementation = com.elevate.auth.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired session token")
    })
    public ResponseEntity<com.elevate.auth.dto.ApiResponse<?>> logoutUser(
            @Parameter(hidden = true) HttpServletRequest httpRequest) {
        com.elevate.auth.dto.ApiResponse<?> response = userService
                .logoutUser((String) httpRequest.getAttribute("sessionKey"));
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

}
