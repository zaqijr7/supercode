package com.supercode.controller;

import com.supercode.request.GeneralRequest;
import com.supercode.service.TransactionService;
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
public class TransactionController {

    @Inject
    TransactionService transactionService;

    @POST
    @Path("/payment/list")
    public Response paymentList() {
        return transactionService.getAllPaymentTransaction();
    }

    @POST
    @Path("/branch/transactions")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response branchTransactions(GeneralRequest request) {
        return transactionService.getBranchTransaction(request);
    }
}
