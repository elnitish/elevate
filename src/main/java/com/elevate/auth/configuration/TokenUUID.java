package com.elevate.auth.configuration;

import java.util.UUID;

public class TokenUUID {
    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }
}
