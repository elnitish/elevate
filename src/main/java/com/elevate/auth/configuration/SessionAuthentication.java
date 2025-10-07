package com.elevate.auth.configuration;

import com.elevate.auth.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class SessionAuthentication implements HandlerInterceptor {

    @Autowired
    private SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // Get UUID from header
        String sessionKey = request.getHeader("Session-Key");

        // Check if header is missing
        if (sessionKey == null || sessionKey.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing session key");
            return false;
        }

        // Check if UUID exists and is valid
        boolean isValid = sessionService.isValidSession(sessionKey);
        if (!isValid) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid session key");
            return false;
        }

        // Allow the request to continue
        return true;
    }
}
