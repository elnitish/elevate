CREATE TABLE employee_attendance (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     employee_id BIGINT NOT NULL,
                                     date DATE NOT NULL,
                                     status ENUM('PENDING','PRESENT', 'ABSENT', 'HALF_DAY') NOT NULL
);
