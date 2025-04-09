package com.supercode.controller;

import com.supercode.request.GeneralRequest;
import com.supercode.service.BranchService;
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
//@Consumes(MediaType.APPLICATION_JSON)
public class BranchController {

    @Inject
    BranchService branchService;

    @Inject
    RekonService rekonService;

    @POST
    @Path("/branch/list")
    public Response rekonProcess() {
        return branchService.getAllBranch();
    }

    @POST
    @Path("/rekon/batch")
    public Response rekonProcess2(GeneralRequest request) {

         return rekonService.rekonBatchProcess(request);
    }
}
