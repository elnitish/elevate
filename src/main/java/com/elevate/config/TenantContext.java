package com.elevate.config;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for extracting tenant information from request attributes.
 * Centralizes the attribute name to prevent casing bugs.
 */
public final class TenantContext {

    private static final String TENANT_ID_ATTR = "tenantID";
    private static final String SESSION_KEY_ATTR = "sessionKey";
    private static final String ROLE_ATTR = "role";

    private TenantContext() {
    }

    public static String getTenantId(HttpServletRequest request) {
        return (String) request.getAttribute(TENANT_ID_ATTR);
    }

    public static String getSessionKey(HttpServletRequest request) {
        return (String) request.getAttribute(SESSION_KEY_ATTR);
    }

    public static String getRole(HttpServletRequest request) {
        return (String) request.getAttribute(ROLE_ATTR);
    }
}
