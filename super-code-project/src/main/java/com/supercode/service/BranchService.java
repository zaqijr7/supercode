package com.supercode.service;

import com.supercode.dto.BranchDTO;
import com.supercode.entity.MasterMerchant;
import com.supercode.repository.MasterMerchantRepository;
import com.supercode.request.GeneralRequest;
import com.supercode.response.BaseResponse;
import com.supercode.util.MessageConstant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BranchService {
    @Inject
    MasterMerchantRepository masterMerchantRepository;

    public Response getAllBranch() {
        BaseResponse baseResponse;
        try {
            List<MasterMerchant> masterMerchantList = masterMerchantRepository.listAll();

            // Konversi ke DTO agar hanya mengembalikan branchId dan branchName
            List<BranchDTO> branchDTOList = masterMerchantList.stream()
                    .map(m -> new BranchDTO(m.getBranchId(), m.getBranchName()))
                    .collect(Collectors.toList());

            baseResponse = new BaseResponse(MessageConstant.SUCCESS_CODE, MessageConstant.SUCCESS_MESSAGE);
            baseResponse.payload = branchDTOList;
            return Response.status(baseResponse.result).entity(baseResponse).build();
        } catch (Exception e) {
            e.printStackTrace();
            baseResponse = new BaseResponse(MessageConstant.FAILED_CODE, MessageConstant.FAILED_MESSAGE);
            return Response.status(baseResponse.result).entity(baseResponse).build();
        }
    }

}
