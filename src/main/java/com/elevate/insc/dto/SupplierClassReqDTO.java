package com.elevate.insc.dto;

import jakarta.persistence.Column;
import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
public class SupplierClassReqDTO {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;


    public SupplierClassReqDTO( String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}