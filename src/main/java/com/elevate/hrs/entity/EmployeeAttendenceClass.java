package com.elevate.hrs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "employee_attendance")
public class EmployeeAttendenceClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // foreign key to EmployeeClass
    private Long employeeId;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private AttendenceStatus status;

    public EmployeeAttendenceClass(long employeeId, LocalDate date, AttendenceStatus status) {
        this.employeeId = employeeId;
        this.date = date;
        this.status = status;
    }

    @Override
    public String toString() {
        return "AttendanceClass{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", date=" + date +
                ", status=" + status +
                '}';
    }
}
