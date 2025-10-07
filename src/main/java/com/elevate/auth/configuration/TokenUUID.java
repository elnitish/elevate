package com.elevate.auth.configuration;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenUUID {
    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }
}
