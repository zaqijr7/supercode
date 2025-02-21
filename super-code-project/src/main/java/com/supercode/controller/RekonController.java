package com.supercode.controller;

import com.supercode.request.GeneralRequest;
import com.supercode.service.RekonService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

    @POST
    @Path("/rekon/summary/data")
    public Response rekonSummaryData(GeneralRequest request) {
        return rekonService.rekonSummaryData(request);
    }
}
