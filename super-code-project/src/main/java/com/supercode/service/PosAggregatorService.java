package com.supercode.service;

import com.supercode.entity.HeaderPayment;
import com.supercode.repository.HeaderPaymentRepository;
import com.supercode.response.BaseResponse;
import com.supercode.response.GeneralResponse;
import com.supercode.util.MessageConstant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@ApplicationScoped
public class PosAggregatorService {



    @Inject
    GeneralService generalService;

    @Transactional
    public Response uploadFile(MultipartFormDataInput file, String paymentType, String pmId, String branchId, String transDate) {

        Response response = null;
        BaseResponse baseResponse;
        try {

            // insert to header table
            String parentId = generalService.saveHeaderPayment(file, paymentType, pmId, branchId, transDate);


            // insert to detail
            generalService.saveDetailPayment(file, paymentType, parentId, pmId, branchId, transDate);

            baseResponse = new BaseResponse(MessageConstant.SUCCESS_CODE, MessageConstant.SUCCESS_MESSAGE);
            return Response.status(baseResponse.getResult()).entity(baseResponse).build();
        }catch(Exception e){
            baseResponse = new BaseResponse(MessageConstant.FAILED_CODE, MessageConstant.FAILED_MESSAGE);
            response = Response.status(baseResponse.getResult()).entity(baseResponse).build();
            e.printStackTrace();
        }


        return response;
    }
}
