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
    @Path("/recon/process")
    public Response rekonProcess(GeneralRequest request) {
        return rekonService.rekonProcess(request);
    }

    @POST
    @Path("/recon/summary")
    public Response rekonSummary(GeneralRequest request) {
        return rekonService.rekonSummary(request);
    }

    @POST
    @Path("/recon/summary/data")
    public Response rekonSummaryData(GeneralRequest request) {
        return rekonService.rekonSummaryData(request);
    }

    /*
        process 2.4 (recon only by branch and date)
     */

    @POST
    @Path("recon/process/compare-branch")
    public Response rekonProcessCompareBranch(GeneralRequest request) {
        return rekonService.rekonProcessCompareBranch(request);
    }

    @POST
    @Path("recon/batch-process")
    public Response rekonBatchProcess(GeneralRequest request) {
        return rekonService.rekonBatchProcess(request);
    }

    @POST
    @Path("recon/bank-aggregator")
    public Response reconBankAggregator(GeneralRequest request) {
        return rekonService.reconBankAggregator(request);
    }
}
