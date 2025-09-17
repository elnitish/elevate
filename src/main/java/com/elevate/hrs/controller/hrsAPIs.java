package com.elevate.hrs.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.hrs.dto.EmployeeAttendanceDTO;
import com.elevate.hrs.entity.EmployeeClass;
import com.elevate.hrs.service.EmployeeAttendenceService;
import com.elevate.hrs.service.EmployeeService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class hrsAPIs {

    private final EmployeeService employeeService;
    private final EmployeeAttendenceService employeeAttendenceService;
    public hrsAPIs(EmployeeService employeeService,EmployeeAttendenceService employeeAttendenceService) {
        this.employeeService = employeeService;
        this.employeeAttendenceService = employeeAttendenceService;
    }

    @GetMapping("/hrs/getAllEmployees")
    public ResponseEntity<?> getAllEmployees(){
        ApiResponse<?> response = employeeService.returnAllEmployees();
        return ResponseEntity.status(HttpStatusCode.valueOf(response.getCode())).body(response);

    }

    @GetMapping("/hrs/getEmployeeWithID/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        ApiResponse<?> response = employeeService.getEmployeeById(id);
        return ResponseEntity.status(HttpStatusCode.valueOf(response.getCode()))
                .body(response);
    }

    // ✅ POST /hr/employees → add a new employee
    @PostMapping("/hrs/addEmployee")
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeClass employee) {
        ApiResponse<?> response = employeeService.addEmployee(employee);
        return ResponseEntity.status(HttpStatusCode.valueOf(response.getCode()))
                .body(response);
    }

    // ✅ PUT /hr/employees/{id} → update employee details
    @PutMapping("/updateEmployeeWithID/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id,
                                            @RequestBody EmployeeClass employee) {
        ApiResponse<?> response = employeeService.updateEmployee(id, employee);
        return ResponseEntity.status(HttpStatusCode.valueOf(response.getCode()))
                .body(response);
    }
    @DeleteMapping("/deleteEmployeeWithID/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        ApiResponse<?> response = employeeService.deleteEmployee(id);
        return ResponseEntity.status(HttpStatusCode.valueOf(response.getCode()))
                .body(response);
    }

    @PostMapping("/markAttendanceOfEmployee")
    public ResponseEntity<?> markAttendenceidID(@RequestBody EmployeeAttendanceDTO employeeAttendanceDTO) {
        ApiResponse<?> response = employeeAttendenceService.markAttendance(employeeAttendanceDTO);
        return ResponseEntity.status(HttpStatusCode.valueOf(response.getCode()))
                .body(response);
    }

    @GetMapping("/getAttendanceOfOneEmployee/{id}")
    public ResponseEntity<?> getAttendanceOfOneEmployee(@PathVariable Long id) {
        ApiResponse<?> response = employeeAttendenceService.returnAttendanceOfID(id);
        return ResponseEntity.status(HttpStatusCode.valueOf(response.getCode()))
                .body(response);
    }

    @GetMapping("/hr/attendance")
    public ResponseEntity<?> getAttendance() {
        ApiResponse<?> response = employeeAttendenceService.returnAllAttendance();
        return ResponseEntity.status(HttpStatusCode.valueOf(response.getCode()))
                .body(response);
    }
}
