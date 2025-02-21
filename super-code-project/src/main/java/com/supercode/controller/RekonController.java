package com.supercode.controller;

import com.supercode.request.GeneralRequest;
import com.supercode.service.RekonService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RekonController {

    @Inject
    RekonService rekonService;


    @POST
    @Path("/rekon/process")
    public Response rekonProcess(GeneralRequest request) {
        return rekonService.rekonProcess(request);
    }

    @POST
    @Path("/rekon/summary")
    public Response rekonSummary(GeneralRequest request) {
        return rekonService.rekonSummary(request);
    }
}
