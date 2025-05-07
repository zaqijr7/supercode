package com.supercode.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supercode.request.GeneralRequest;
import com.supercode.request.MyJsonData;
import com.supercode.response.BaseResponse;
import com.supercode.service.PosAggregatorService;
import com.supercode.service.RekonService;
import com.supercode.util.MessageConstant;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.util.List;
import java.util.Map;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PosAggregatorController {

    @Inject
    PosAggregatorService posAggregatorService;


    /*
    * upload header for pos
    * */
   /* @POST
    @Path("/upload/file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadHeaderPos(@MultipartForm MultipartFormDataInput input, @QueryParam("pmId") String pmId,
                                    @QueryParam("branchId") String branchId) {
        return posAggregatorService.uploadFile(input, pmId, branchId);
    }*/

    @POST
    @Path("/upload/file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadHeaderPos(@MultipartForm MultipartFormDataInput input) {
        BaseResponse baseResponse;
        try {
            Map<String, List<InputPart>> formParts = input.getFormDataMap();

            String jsonData = null;
            if (formParts.containsKey("jsonData")) {
                InputPart jsonPart = formParts.get("jsonData").get(0);
                jsonData = jsonPart.getBodyAsString();
            }

            if (jsonData == null || jsonData.trim().isEmpty()) {
                baseResponse = new BaseResponse(MessageConstant.BAD_REQUEST,MessageConstant.JSON_DATA_MISSING);
                return Response.status(baseResponse.result)
                        .entity(baseResponse)
                        .build();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            MyJsonData myJsonObject = objectMapper.readValue(jsonData, MyJsonData.class);
            return posAggregatorService.uploadFile(input, myJsonObject.getPaymentType(), myJsonObject.getPmId(), myJsonObject.getBranchId(), myJsonObject.getTransDate(), myJsonObject.getUser());

        } catch (JsonProcessingException e) {
            baseResponse = new BaseResponse(MessageConstant.BAD_REQUEST,MessageConstant.INVALID_JSON_FORMAT);
            return Response.status(baseResponse.result)
                    .entity(baseResponse)
                    .build();
        } catch (Exception e) {
            baseResponse = new BaseResponse(MessageConstant.FAILED_CODE,MessageConstant.FAILED_MESSAGE);
            return Response.status(baseResponse.result)
                    .entity(baseResponse)
                    .build();
        }
    }
}
