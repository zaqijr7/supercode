package com.supercode.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable ex) {
        ex.printStackTrace(); // cek class exception yang dilempar
        return Response.status(500).entity("Global error: " + ex.getMessage()).build();
    }
}
