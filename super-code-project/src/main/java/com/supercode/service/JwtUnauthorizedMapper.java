package com.supercode.service;

import com.supercode.response.BaseResponse;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


@Provider
@Priority(1)
@ApplicationScoped
public class JwtUnauthorizedMapper implements ExceptionMapper<NotAuthorizedException> {

    @Override
    public Response toResponse(NotAuthorizedException exception) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.result= 401;
        baseResponse.message = "Unauthorized: Invalid or missing authentication token";

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(baseResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


}
