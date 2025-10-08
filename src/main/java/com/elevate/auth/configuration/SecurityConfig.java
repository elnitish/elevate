package com.elevate.auth.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    SessionAuthentication sessionAuthentication;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionAuthentication)
                .addPathPatterns("/**") // Apply to all paths
                .excludePathPatterns(
                    // Authentication endpoints (no session required)
                    "/auth/tenantRegister", //create a new tenant
                    "/auth/createUser",  // User creation endpoint
                    "/auth/userLogin", //login a user in an organisation

                    // Public endpoints that don't require authentication
                    "/auth/validate-token/**",  // Token validation endpoint
                    
                    // Static resources and health checks
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/actuator/**",
                    "/health",
                    "/error"
                );
    }

}
