package com.supercode.service;

import com.supercode.entity.HeaderPayment;
import com.supercode.repository.*;
import com.supercode.request.GeneralRequest;
import com.supercode.response.BaseResponse;
import com.supercode.util.MessageConstant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hibernate.event.spi.SaveOrUpdateEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Inject
    GeneralService generalService;

    @Transactional
    public Response rekonProcess(GeneralRequest request) {
        BaseResponse baseResponse;
        String branchId = request.getBranchId();
        try {
            List<String> pmIds =  paymentMethodRepository.getPaymentMethods();
            for(String pmId : pmIds){
                request.setPmId(pmId);
                int countDataPos = posRepository.getCountDataPost(request, branchId, pmId);
                List<BigDecimal> grossAmounts = posRepository.getAllGrossAmount(request, branchId);
                int countDataAggregator = detailPaymentAggregatorRepository.getCountDataAggregator(request, branchId, grossAmounts);
                request.setBranchId(branchId);
                List<BigDecimal> grossAmountEcom = detailPaymentAggregatorRepository.getAllGrossAmount(request);

                if(countDataPos!=0 && countDataAggregator!=0){
                    if(countDataPos<countDataAggregator){

                        detailPaymentAggregatorRepository.updateFlagByCondition(request, grossAmounts);
                        posRepository.updateFlagNormalByCondition(request);
                    }else if(countDataAggregator<countDataPos){
                        posRepository.updatePosFlag(request, grossAmountEcom);
                        detailPaymentAggregatorRepository.updateFlagNormalByCondition(request, grossAmounts);
                    }else{
                        List<Long> detailAgg = detailPaymentAggregatorRepository.getDetailIdByRequest(request, grossAmounts);
                        List<Long> detailPos = posRepository.getDetailPosId(request, branchId, pmId);
                        String updatedVersion = MessageConstant.TWO_VALUE;
                        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
                            updatedVersion = MessageConstant.ONE_VALUE;
                        }
                        int indexPos = 0;
                        for(Long detailAggStr : detailAgg ){
                            detailPaymentAggregatorRepository.updateData(detailAggStr, updatedVersion);
                            posRepository.updateDataPos(detailAggStr, updatedVersion, detailPos.get(indexPos));
                            indexPos++;
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

    @Transactional
    public Response rekonSummaryData(GeneralRequest request) {
        BaseResponse baseResponse;
        try {
            // get parent_id by trans date
            List<HeaderPayment> headerPayments = headerPaymentRepository.getByTransDateAndBranchId(request.getTransDate(), request.getBranchId());
            for(HeaderPayment hp  : headerPayments){
                String pmName = paymentMethodRepository.getPaymentMethodByPmId(hp.getPmId());
                if(pmName.equalsIgnoreCase(MessageConstant.POS)){
                    int countFailedPos = posRepository.getCountFailedByParentId(hp.getParentId());
                    if(countFailedPos==0){
                        headerPaymentRepository.updateHeader(hp.getParentId());
                    }
                }else{
                    int countFailedAggregator= detailPaymentAggregatorRepository.getFailedRecon(hp.getParentId());
                    if(countFailedAggregator==0){
                        headerPaymentRepository.updateHeaderEcom(hp.getParentId());
                    }

                }
            }

            /*List<String> pmIds =  paymentMethodRepository.getPaymentMethods();
            boolean checkStatus = true;
            for(String pmId : pmIds){
                // get data pos
                request.setPmId(pmId);
                int countFailedPos = posRepository.getCountFailed(pmId,request.getTransDate());

                if(countFailedPos>0){
                    checkStatus=false;
                    break;
                }

            }
            if(checkStatus){
                // update rekon summary
                headerPaymentRepository.updateHeaderPaymentByCondition(request.getTransDate());
            }*/

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
    public Response rekonProcessCompareBranch(GeneralRequest request) {
        BaseResponse baseResponse;
        // get data payment method
        try {
            int countDataPos = posRepository.getCountDataPostByBranch(request);
            List<BigDecimal> grossAmounts = posRepository.getAllGrossAmountByBranch(request);
            // get data aggregator
            int countDataAggregator = detailPaymentAggregatorRepository.getCountDataAggregatorByBranch(request, grossAmounts);
            List<BigDecimal> grossAmountEcom = detailPaymentAggregatorRepository.getAllGrossAmountByBranch(request);

            if(countDataPos!=0 && countDataAggregator!=0){
                if(countDataPos<countDataAggregator){
                    detailPaymentAggregatorRepository.updateFlagByBranchCondition(request, grossAmounts);
                    posRepository.updateFlagNormalByBranchCondition(request);
                }else if(countDataAggregator<countDataPos){
                    posRepository.updatePosFlagByBranch(request, grossAmountEcom);
                    detailPaymentAggregatorRepository.updateFlagNormalByBranchCondition(request, grossAmounts);
                }else{
                    List<Long> detailAgg = detailPaymentAggregatorRepository.getDetailIdByRequestByBranch(request, grossAmounts);
                    List<Long> detailPos = posRepository.getDetailPosIdByBranch(request);
                    String updatedVersion = MessageConstant.THREE_VALUE;
                    System.out.println(detailAgg);
                    System.out.println(detailPos);
                    int indexPos = 0;
                    for(Long detailAggStr : detailAgg ){
                        detailPaymentAggregatorRepository.updateData(detailAggStr, updatedVersion);
                        posRepository.updateDataPos(detailAggStr, updatedVersion, detailPos.get(indexPos));
                        indexPos++;
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
    public Response rekonBatchProcess(GeneralRequest request) {
        BaseResponse baseResponse;

        // save data to submit process

        try {
            generalService.saveDataLog(request);
            // function 2.1
            generalService.processTransTime(request);
            request.setTransTime(null);
            // function 2.2
            generalService.processWithoutTransTime(request);

            // function 2.4
            request.setPmId(null);
            generalService.processWithTransDateAndBranch(request);



            // function 2.5 recon ecommerce to bank
            generalService.reconBankAggregator(request);
            // function 2.3
            generalService.summaryReconEcom2Pos(request);


            List<HeaderPayment> headerPayments = headerPaymentRepository.getByTransDateAndBranchId(
                    request.getTransDate(), request.getBranchId());

            LocalDateTime now = LocalDateTime.now();
            String timeOnly = now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            for (HeaderPayment hp : headerPayments) {
                hp.setChangedAt(timeOnly);
                hp.setChangedOn(now);
                headerPaymentRepository.updateHeaderChange(hp);
            }
//            headerPaymentRepository.flush();

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

    public Response reconBankAggregator(GeneralRequest request) {
        BaseResponse baseResponse;
        try {
            // function 2.1
            generalService.reconBankAggregator(request);
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


}
