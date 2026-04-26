package com.elevate.config;

import com.elevate.auth.configuration.SessionAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionAuthentication sessionAuthentication;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionAuthentication)
                .addPathPatterns("/**") // Apply to all paths
                .excludePathPatterns(
                        "/auth/**", // Exclude login/register
                        "/error",
                        "/v3/api-docs/**",
                        "/swagger-ui/**");
    }
}
