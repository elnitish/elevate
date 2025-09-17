package com.elevate.hrs.dto;

import com.elevate.hrs.entity.AttendenceStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

//POJO
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeAttendanceDTO {

    private long employeeId;
    private AttendenceStatus status;
    private LocalDate date;
}
