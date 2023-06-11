package com.example.babymonitoringcw.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import javax.ws.rs.core.HttpHeaders;
import java.util.Base64;


@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String PHYSICIAN_USERNAME = "physician";
    private static final String PHYSICIAN_PASSWORD = "physician123";

    @Override
    public void filter(ContainerRequestContext requestContext) {

        // Get the Authorization header from the request
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the Authorization header is valid
        if (isValidAuthHeader(authHeader)) {
            // Check the role based on the provided username and password
            String role = getRoleFromAuthHeader(authHeader);
            if (role != null) {
                if (isRolePermittedForMethod(role, requestContext.getMethod())) {
                    return;
                }
            }
        }

        // Invalid or missing authentication header, or role not permitted return a 401 uanauthorized response
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    private boolean isValidAuthHeader(String authHeader) {
        return authHeader != null && authHeader.startsWith("Basic");
    }

    private String getRoleFromAuthHeader(String authHeader) {
        // Extract the username and password from the authentication header
        String encodedCredentials = authHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(encodedCredentials));
        System.out.println(credentials);
        String[] parts = credentials.split(":");
        String username = parts[0];
        String password = parts[1];

        // Check if the username and password match a known role
        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            return "ADMIN";
        } else if (username.equals(PHYSICIAN_USERNAME) && password.equals(PHYSICIAN_PASSWORD)) {
            return "PHYSICIAN";
        }

        return null;
    }

    private boolean isRolePermittedForMethod(String role, String method) {
        // - If the role is "ADMIN", allow all methods
        // - If the role is "PHYSICIAN", allow only "GET" methods
        // - For other roles or methods, return false
        if (role.equals("ADMIN")) {
            return true;
        } else if (role.equals("PHYSICIAN") && method.equals("GET")) {
            return true;
        }
        return false;
    }
}
