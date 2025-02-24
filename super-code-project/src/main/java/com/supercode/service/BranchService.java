package com.supercode.service;

import com.supercode.entity.MasterMerchant;
import com.supercode.repository.MasterMerchantRepository;
import com.supercode.request.GeneralRequest;
import com.supercode.response.BaseResponse;
import com.supercode.util.MessageConstant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class BranchService {
    @Inject
    MasterMerchantRepository masterMerchantRepository;

    public Response getAllBranch(GeneralRequest request) {
        BaseResponse baseResponse;
        try {
            List<MasterMerchant> masterMerchantList = masterMerchantRepository.listAll();
            baseResponse = new BaseResponse(MessageConstant.SUCCESS_CODE,MessageConstant.SUCCESS_MESSAGE);
            baseResponse.payload = masterMerchantList;
            return Response.status(baseResponse.result).entity(baseResponse).build();
        }catch (Exception e){
            e.printStackTrace();
            baseResponse = new BaseResponse(MessageConstant.FAILED_CODE,MessageConstant.FAILED_MESSAGE);
            return Response.status(baseResponse.result)
                    .entity(baseResponse)
                    .build();
        }

    }
}
