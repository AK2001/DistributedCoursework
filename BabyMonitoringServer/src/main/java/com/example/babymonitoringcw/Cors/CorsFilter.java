package com.example.babymonitoringcw.Cors;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        responseContext.getHeaders().add(  "Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add(  "Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add(  "Access-Control-Allow-Headers",     "origin, content-type, accept, authorization");
        responseContext.getHeaders().add(  "Access-Control-Allow-Methods",     "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().add(  "Access-Control-Max-Age", "1209600");

        if (requestContext.getMethod().equals("OPTIONS")) {
            // Handle the OPTIONS method by returning an HTTP 200 OK status
            responseContext.setStatus(Response.Status.OK.getStatusCode());
        }
    }
}