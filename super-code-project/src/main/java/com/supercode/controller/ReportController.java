package com.supercode.controller;

import com.supercode.service.ReportService;
import com.supercode.request.GeneralRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/recon")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportController {

    @Inject
    ReportService reportService;


    @POST
    @Path("/report/recon-pos-ecom")
    public Response reportReconPosEcom(GeneralRequest request) {
        return reportService.getDataReportPosVsEcom(request);
    }

    @POST
    @Path("/report/recon-pos-ecom/ext")
    public Response reportReconPosEcom2(GeneralRequest request) {
        return reportService.getDataReportPosVsEcom2(request);
    }
}
