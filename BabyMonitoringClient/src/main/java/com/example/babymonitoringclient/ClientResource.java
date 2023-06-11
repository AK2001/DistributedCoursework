package com.example.babymonitoringclient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/clientResources")
public class ClientResource{
    @GET
    @Produces("text/plain")
    public String greet(){
        return "Hello";
    }
}