package com.elevate.hrs.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.hrs.dto.EmployeeAttendanceDTO;
import com.elevate.hrs.entity.EmployeeAttendenceClass;
import com.elevate.hrs.entity.EmployeeClass;
import com.elevate.hrs.repository.EmployeeAttendenceRepo;
import com.elevate.hrs.repository.EmployeeClassRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeClassRepo employeeClassRepo;
    private final EmployeeAttendenceRepo employeeAttendenceRepo;

    public EmployeeService(EmployeeClassRepo employeeClassRepo, EmployeeAttendenceRepo employeeAttendenceRepo) {
        this.employeeClassRepo = employeeClassRepo;
        this.employeeAttendenceRepo = employeeAttendenceRepo;
    }

    public ApiResponse<?> returnAllEmployees() {
        List<EmployeeClass> employees = employeeClassRepo.findAll();
        return new ApiResponse<>("All employees fetched successfully",200,employees);
    }

    public ApiResponse<?> getEmployeeById(Long id) {
        Optional<EmployeeClass> employee = employeeClassRepo.findById(id);
        if (employee.isPresent()) {
            return new ApiResponse<>("Employee with id " + id,200,employee.get());
        }
        return new ApiResponse<>("No Employee with id " + id,404,null);
    }

    public ApiResponse<?> addEmployee(EmployeeClass employee) {
        employeeClassRepo.save(employee);
        return new ApiResponse<>("Employee added successfully",200,employee);
    }

    public ApiResponse<?> updateEmployee(Long id, EmployeeClass employee) {
        Optional<EmployeeClass> employeeClass = employeeClassRepo.findById(id);
        if (employeeClass.isEmpty()) {
            return new ApiResponse<>("No Employee with id " + id,404,null);
        }
        EmployeeClass existing = employeeClass.get();
        existing.setName(employee.getName());
        existing.setEmail(employee.getEmail());
        existing.setPhone(employee.getPhone());
        existing.setDesignation(employee.getDesignation());
        existing.setDepartment(employee.getDepartment());
        existing.setDateOfJoining(employee.getDateOfJoining());
        existing.setSalary(employee.getSalary());
        employeeClassRepo.save(existing);
        return new ApiResponse<>("Employee updated successfully",200,employee);
    }

    public ApiResponse<?> deleteEmployee(Long id) {
        Optional<EmployeeClass> employee = employeeClassRepo.findById(id);
        if (employee.isEmpty()) {
            return new ApiResponse<>("No Employee with id " + id,404,null);

        }
        employeeClassRepo.delete(employee.get());
        return new ApiResponse<>("Employee deleted successfully",200,employee);
    }


}
