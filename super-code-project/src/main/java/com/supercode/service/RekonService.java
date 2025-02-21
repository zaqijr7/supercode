package com.supercode.service;

import com.supercode.repository.*;
import com.supercode.request.GeneralRequest;
import com.supercode.response.BaseResponse;
import com.supercode.util.MessageConstant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class RekonService {

    @Inject
    PosRepository posRepository;

    @Inject
    DetailPaymentAggregatorRepository detailPaymentAggregatorRepository;

    @Inject
    PaymentMethodRepository paymentMethodRepository;

    @Inject
    HeaderPaymentRepository headerPaymentRepository;

    @Inject
    MasterMerchantRepository masterMerchantRepository;

    @Transactional
    public Response rekonProcess(GeneralRequest request) {
        BaseResponse baseResponse;
        // get data payment method
        try {
            List<String> pmIds =  paymentMethodRepository.getPaymentMethods();
            for(String pmId : pmIds){
                // get data branch for every pm
                List<String> branchIds = masterMerchantRepository.getBranchId();
                // get data pos
                request.setPmId(pmId);
                for(String branchId : branchIds){
                    int countDataPos = posRepository.getCountDataPost(request, branchId, pmId);
                    List<BigDecimal> grossAmounts = posRepository.getAllGrossAmount(request, branchId);
                    // get data aggregator
                    int countDataAggregator = detailPaymentAggregatorRepository.getCountDataAggregator(request, branchId, grossAmounts);
                    request.setBranchId(branchId);
                    List<BigDecimal> grossAmountEcom = detailPaymentAggregatorRepository.getAllGrossAmount(request);

                    if(countDataPos!=0 && countDataAggregator!=0){
                        // select parent id
//                        String parent_id = posRepository.getParentId(request, branchId, pmId);
                        if(countDataPos<countDataAggregator){
                            System.out.println("masuk sini apa");
                            detailPaymentAggregatorRepository.updateFlagByCondition(request, grossAmounts);
                            posRepository.updateFlagNormalByCondition(request);
                        }else if(countDataAggregator<countDataPos){
                            System.out.println("masuk siniii "+ grossAmountEcom);
                            posRepository.updatePosFlag(request, grossAmountEcom);
                            detailPaymentAggregatorRepository.updateFlagNormalByCondition(request, grossAmounts);
                        }else{
                            System.out.println("apa masuk sini");
                            detailPaymentAggregatorRepository.updateFlagNormalByCondition(request, grossAmounts);
                            posRepository.updateFlagNormalByCondition(request);
                        }


                    }
                }

            }

            baseResponse = new BaseResponse(MessageConstant.SUCCESS_CODE,MessageConstant.SUCCESS_MESSAGE);
            return Response.status(baseResponse.result).entity(baseResponse).build();
        }catch (Exception e){
            e.printStackTrace();
            baseResponse = new BaseResponse(MessageConstant.FAILED_CODE,MessageConstant.FAILED_MESSAGE);
            return Response.status(baseResponse.result)
                    .entity(baseResponse)
                    .build();
        }

    }

    @Transactional
    public Response rekonSummary(GeneralRequest request) {
        BaseResponse baseResponse;
        try {
            List<String> pmIds =  paymentMethodRepository.getPaymentMethods();
            boolean checkStatus = true;
            for(String pmId : pmIds){
                // get data pos
                request.setPmId(pmId);
                int countFailedPos = posRepository.getCountFailed(pmId,request.getTransDate());
                System.out.println("Gagal "+ countFailedPos);
                if(countFailedPos>0){
                    checkStatus=false;
                    break;
                }

            }
            if(checkStatus){
                // update rekon summary
                headerPaymentRepository.updateHeaderPaymentByCondition(request.getTransDate());
            }

            baseResponse = new BaseResponse(MessageConstant.SUCCESS_CODE,MessageConstant.SUCCESS_MESSAGE);
            return Response.status(baseResponse.result).entity(baseResponse).build();
        }catch (Exception e){
            baseResponse = new BaseResponse(MessageConstant.FAILED_CODE,MessageConstant.FAILED_MESSAGE);
            return Response.status(baseResponse.result)
                    .entity(baseResponse)
                    .build();
        }

    }
}
