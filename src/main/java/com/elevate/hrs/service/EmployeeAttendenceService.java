package com.elevate.hrs.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.hrs.dto.EmployeeAttendanceDTO;
import com.elevate.hrs.entity.EmployeeAttendenceClass;
import com.elevate.hrs.entity.EmployeeClass;
import com.elevate.hrs.repository.EmployeeAttendenceRepo;
import com.elevate.hrs.repository.EmployeeClassRepo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeAttendenceService {

    private final EmployeeClassRepo employeeClassRepo;
    private final EmployeeAttendenceRepo employeeAttendenceRepo;

    public EmployeeAttendenceService(EmployeeAttendenceRepo employeeAttendenceRepo, EmployeeClassRepo employeeClassRepo) {
        this.employeeAttendenceRepo = employeeAttendenceRepo;
        this.employeeClassRepo = employeeClassRepo;
    }

    public ApiResponse<?> markAttendance(EmployeeAttendanceDTO employeeAttendanceDTO) {
        Optional<EmployeeClass> employee = employeeClassRepo.findById(employeeAttendanceDTO.getEmployeeId());
        if (employee.isEmpty()) {
            return new ApiResponse<>("No Employee with id " + employeeAttendanceDTO.getEmployeeId(), 404, null);
        }
        EmployeeClass employeeClass = employee.get();
        EmployeeAttendenceClass newEmployeeAttendance = new EmployeeAttendenceClass(
                employeeAttendanceDTO.getEmployeeId(),
                employeeAttendanceDTO.getDate(),
                employeeAttendanceDTO.getStatus()
        );
        employeeAttendenceRepo.save(newEmployeeAttendance);
        return new ApiResponse<>("Attendance marked successfully", 200, newEmployeeAttendance);
    }

    public ApiResponse<?> returnAttendanceOfID(Long id) {
        List<EmployeeAttendenceClass> attendance = employeeAttendenceRepo.findAllByEmployeeId(id, Sort.by(Sort.Direction.DESC, "date"));
        if (attendance.isEmpty()) {
            return new ApiResponse<>("No Employee Attendance with id " + id, 404, null);
        }
        return new ApiResponse<>("Attendance fetched successfully", 200, attendance);
    }

    public ApiResponse<?> returnAllAttendance() {
        List<EmployeeAttendenceClass> attendance = employeeAttendenceRepo.findAll(Sort.by(Sort.Direction.DESC, "date"));
        if (attendance.isEmpty()) {
            return new ApiResponse<>("No Employee Attendance", 404, null);
        }
        return new ApiResponse<>("Attendance fetched successfully", 200, attendance);
    }
}
