package com.supercode.service;

import com.supercode.dto.PosReportDto;
import com.supercode.request.GeneralRequest;
import com.supercode.entity.DetailPaymentPos;
import com.supercode.repository.DetailPaymentAggregatorRepository;
import com.supercode.repository.PosRepository;
import com.supercode.response.BaseResponse;
import com.supercode.util.MessageConstant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ReportService {

    @Inject
    PosRepository posRepository;

    @Inject
    DetailPaymentAggregatorRepository detailPaymentAggregatorRepository;


    public Response getDataReportPosVsEcom(GeneralRequest request) {
        BaseResponse baseResponse;
        try {
            PosReportDto.PosReport posReport= new PosReportDto.PosReport();
            String parentId = posRepository.getLatestParentId(request);
            if(null != parentId){
                List<PosReportDto> posDataList = posRepository.getDataWithOffset(request, parentId);
                posReport.setPostReportDtos(posDataList);
                // count total data
                int totalData = posRepository.getTotalPost(parentId);
                BigDecimal totalAmountPos = posRepository.getAmountByParentId(parentId);
                BigDecimal totalAmountAgg = detailPaymentAggregatorRepository.getGrossAmountByParentId(parentId);
                posReport.setTotalData(totalData);
                posReport.setTotalPos(totalAmountPos==null? BigDecimal.valueOf(0):totalAmountPos);
                posReport.setTotalAgg(totalAmountAgg==null? BigDecimal.valueOf(0) :totalAmountAgg);
                BigDecimal diff = posReport.getTotalPos().subtract(posReport.getTotalAgg()).abs();
                posReport.setDiff(diff);
                baseResponse = new BaseResponse(MessageConstant.SUCCESS_CODE, MessageConstant.SUCCESS_MESSAGE);
                baseResponse.payload = posReport;
            }else{
                baseResponse = new BaseResponse(MessageConstant.DATA_NOT_FOUND, "Data Not Found");
            }
            return Response.status(baseResponse.result).entity(baseResponse).build();

        }catch (Exception e){
            e.printStackTrace();
            baseResponse = new BaseResponse(MessageConstant.FAILED_CODE, MessageConstant.FAILED_MESSAGE);
            return Response.status(baseResponse.result).entity(baseResponse).build();
        }


    }
}
