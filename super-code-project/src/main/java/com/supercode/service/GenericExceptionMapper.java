package com.supercode.service;

import com.supercode.response.BaseResponse;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<jakarta.ws.rs.NotAuthorizedException> {




    @Override
    public Response toResponse(NotAuthorizedException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("result", 401);
        result.put("message", "Unauthorized");
        result.put("error", e.getMessage());

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(result)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
