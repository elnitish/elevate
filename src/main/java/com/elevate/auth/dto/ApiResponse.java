package com.elevate.auth.dto;


import lombok.Data;

@Data
public class ApiResponse<T> {
    String message;
    int code;
    T data;

    public ApiResponse() {
    }

    public ApiResponse(String message, int code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return this.code;
    }
}
