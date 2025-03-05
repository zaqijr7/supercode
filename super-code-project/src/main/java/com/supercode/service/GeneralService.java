package com.supercode.service;

import com.supercode.entity.DetailPaymentAggregator;
import com.supercode.entity.DetailPaymentPos;
import com.supercode.entity.HeaderPayment;
import com.supercode.entity.LogRecon;
import com.supercode.repository.*;
import com.supercode.request.GeneralRequest;
import com.supercode.response.BaseResponse;
import com.supercode.util.MessageConstant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@ApplicationScoped
public class GeneralService {

    @Inject
    HeaderPaymentRepository headerPaymentRepository;

    @Inject
    PaymentMethodRepository paymentMethodRepository;

    @Inject
    PosRepository posRepository;

    @Inject
    DetailPaymentAggregatorRepository detailPaymentAggregatorRepository;

    @Inject
    BankMutationRepository bankMutationRepository;

    @Inject
    LogReconRepository logReconRepository;




    public String saveHeaderPayment(MultipartFormDataInput file, String paymentWay, String pmId) {
        String parentId = generateRandomCode();
        Map<String, List<InputPart>> formDataMap = file.getFormDataMap();
        List<InputPart> fileParts = formDataMap.get("file");
        HeaderPayment headerPayment = new HeaderPayment();

        if (paymentWay.equalsIgnoreCase(MessageConstant.POS)) {
            paymentWay = paymentMethodRepository.getPaymentIdByPaymentMethod(paymentWay);
            headerPayment.setPmId(paymentWay);
        } else {
            headerPayment.setPmId(pmId);
        }
        InputPart filePart = fileParts.get(0);
        headerPayment.setFileName(getFileName(filePart));
        headerPayment.setParentId(parentId);
        headerPaymentRepository.persist(headerPayment);
        return parentId;

    }

    private String getFileName(InputPart inputPart) {
        try {
            Map<String, List<String>> headers = inputPart.getHeaders();
            String contentDisposition = headers.get("Content-Disposition").get(0);

            for (String content : contentDisposition.split(";")) {
                if (content.trim().startsWith("filename")) {
                    return content.split("=")[1].trim().replaceAll("\"", "");
                }
            }
        } catch (Exception e) {
            return "unknown_file";
        }
        return "unknown_file";
    }

    public void saveDetailPayment(MultipartFormDataInput file, String paymentType, String parentId, String pmId) {
        try {
            if (paymentType.equalsIgnoreCase(MessageConstant.POS)) {
                saveDetailPos(file, parentId);
            } else {
                String paymentMethod = paymentMethodRepository.getPaymentMethodByPmId(pmId);
                if (paymentMethod.equalsIgnoreCase(MessageConstant.SHOPEEFOOD)) {
                    saveDetailShopeeFood(file, pmId, parentId);
                }
                /*else if(paymentMethod.equalsIgnoreCase(MessageConstant.GRABFOOD)){
                    saveDetailGrabFood(file, pmId, branchId);
                }*/
            }
        } catch (Exception e) {

        }
    }

    private void saveDetailGrabFood(MultipartFormDataInput file, String pmId, String branchId) {
        try {
            InputPart inputPart = getInputPart(file);
            try (InputStream inputStream = inputPart.getBody(InputStream.class, null);
                 Workbook workbook = new XSSFWorkbook(inputStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;
                    String transId = row.getCell(1).getStringCellValue();
                    Cell timeTransCell = row.getCell(2);
                    String formattedTimeDate = getDate(timeTransCell);

                    Cell timeCell = row.getCell(2);
                    String formattedTime = getTime(timeCell);


                    Cell grossAmountCell = row.getCell(3);
                    BigDecimal grossAmount = null;
                    if (grossAmountCell.getCellType() == CellType.NUMERIC) {
                        grossAmount = BigDecimal.valueOf(grossAmountCell.getNumericCellValue());
                    } else if (grossAmountCell.getCellType() == CellType.STRING) {
                        grossAmount = new BigDecimal(grossAmountCell.getStringCellValue());
                    }
                    Cell nettAmountCell = row.getCell(2);
                    BigDecimal nettAmount = null;
                    if (nettAmountCell.getCellType() == CellType.NUMERIC) {
                        nettAmount = BigDecimal.valueOf(nettAmountCell.getNumericCellValue());
                    } else if (nettAmountCell.getCellType() == CellType.STRING) {
                        nettAmount = new BigDecimal(nettAmountCell.getStringCellValue());
                    }

                    DetailPaymentAggregator dpa = new DetailPaymentAggregator();
                    dpa.setBranchId(branchId);
                    dpa.setPmId(pmId);
                    dpa.setTransDate(formattedTimeDate);
                    dpa.setTransId(transId);
                    dpa.setTransTime(formattedTime);
                    dpa.setGrossAmount(grossAmount);
                    dpa.setNetAmount(nettAmount);
                    dpa.setCharge(dpa.getGrossAmount().subtract(dpa.getNetAmount()));
                    dpa.setPaymentId(dpa.getTransId() + dpa.getPmId());
                    dpa.setSettlementDate(formattedTimeDate);
                    dpa.setSettlementTime(formattedTime);
                    detailPaymentAggregatorRepository.persist(dpa);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InputPart getInputPart(MultipartFormDataInput file) {
        Map<String, List<InputPart>> fileMap = file.getFormDataMap();

        List<InputPart> fileParts = fileMap.get("file"); // Sesuaikan key dengan yang dikirim di Postman
        if (fileParts == null || fileParts.isEmpty()) {
            throw new IllegalArgumentException("File Not Found!");
        }

        InputPart inputPart = fileParts.get(0);
        return inputPart;
    }


    private void saveDetailPos(MultipartFormDataInput file, String parentId) {
        try {
            InputPart inputPart = getInputPart(file);
            try (InputStream inputStream = inputPart.getBody(InputStream.class, null);
                 Workbook workbook = new XSSFWorkbook(inputStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;
                    if (row == null) break;
                    Cell pmIdCell = row.getCell(4);
                    String transId = row.getCell(5).getStringCellValue();
                    String branchId = row.getCell(0).getStringCellValue();
                    String pmId = "";
                    if (pmIdCell.getCellType() == CellType.NUMERIC) {
                        DecimalFormat df = new DecimalFormat("#");
                        pmId = df.format(pmIdCell.getNumericCellValue());
                    } else if (pmIdCell.getCellType() == CellType.STRING) {
                        pmId = pmIdCell.getStringCellValue();
                    }

                    Cell grossAmountCell = row.getCell(3);
                    BigDecimal grossAmount = null;
                    if (grossAmountCell.getCellType() == CellType.NUMERIC) {
                        grossAmount = BigDecimal.valueOf(grossAmountCell.getNumericCellValue());
                    } else if (grossAmountCell.getCellType() == CellType.STRING) {
                        grossAmount = new BigDecimal(grossAmountCell.getStringCellValue());
                    }
                    Cell timeTransCell = row.getCell(1);
                    String formattedTimeDate = getDate(timeTransCell);

                    Cell timeCell = row.getCell(2);
                    String formattedTime = getTime(timeCell);
                    DetailPaymentPos detailPaymentPos = new DetailPaymentPos();
                    detailPaymentPos.setPmId(pmId);
                    detailPaymentPos.setBranchId(branchId);
                    detailPaymentPos.setTransDate(formattedTimeDate);
                    detailPaymentPos.setTransId(transId);
                    detailPaymentPos.setTransTime(formattedTime);
                    detailPaymentPos.setGrossAmount(grossAmount);
                    detailPaymentPos.setParentId(parentId);
                    detailPaymentPos.setPayMethodAggregator(row.getCell(4).getStringCellValue());
                    posRepository.persist(detailPaymentPos);
                }

                // update header payment
                String getTransDate = posRepository.getTransDateByParentId(parentId);
                headerPaymentRepository.updateDate(parentId, getTransDate);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void saveDetailShopeeFood(MultipartFormDataInput file, String pmId, String parentId) {
        try {
            InputPart inputPart = getInputPart(file);
            try (InputStream inputStream = inputPart.getBody(InputStream.class, null);
                 Workbook workbook = new XSSFWorkbook(inputStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;
                    String transId = row.getCell(4).getStringCellValue();
                    Cell timeTransCell = row.getCell(2);
                    String formattedTimeDate = getDate(timeTransCell);

                    Cell timeCell = row.getCell(3);
                    String formattedTime = getTime(timeCell);
                    String branchId = row.getCell(1).getStringCellValue();

                    Cell grossAmountCell = row.getCell(5);
                    BigDecimal grossAmount = null;
                    if (grossAmountCell.getCellType() == CellType.NUMERIC) {
                        grossAmount = BigDecimal.valueOf(grossAmountCell.getNumericCellValue());
                    } else if (grossAmountCell.getCellType() == CellType.STRING) {
                        grossAmount = new BigDecimal(grossAmountCell.getStringCellValue());
                    }
                    Cell nettAmountCell = row.getCell(6);
                    BigDecimal nettAmount = null;
                    if (nettAmountCell.getCellType() == CellType.NUMERIC) {
                        nettAmount = BigDecimal.valueOf(nettAmountCell.getNumericCellValue());
                    } else if (nettAmountCell.getCellType() == CellType.STRING) {
                        nettAmount = new BigDecimal(nettAmountCell.getStringCellValue());
                    }

                    DetailPaymentAggregator dpa = new DetailPaymentAggregator();
                    dpa.setBranchId(branchId);
                    dpa.setPmId(pmId);
                    dpa.setTransDate(formattedTimeDate);
                    dpa.setTransId(transId);
                    dpa.setTransTime(formattedTime);
                    dpa.setGrossAmount(grossAmount);
                    dpa.setNetAmount(nettAmount);
                    dpa.setParentId(parentId);
                    //dpa.setCharge(dpa.getGrossAmount().subtract(dpa.getNetAmount()));
                    //dpa.setPaymentId(dpa.getTransId()+dpa.getPmId());
                    dpa.setSettlementTime(formattedTime);
                    detailPaymentAggregatorRepository.persist(dpa);
                }
// update header payment
                String getTransDate = detailPaymentAggregatorRepository.getTransDateByParentId(parentId);
                headerPaymentRepository.updateDate(parentId, getTransDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDate(Cell timeTransCell) {
        Date dateTime = timeTransCell.getDateCellValue();
        SimpleDateFormat timeFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        String formattedTimeDate = timeFormatDate.format(dateTime);
        return formattedTimeDate;
    }

    private String getTime(Cell timeTransCell) {
        Date date = timeTransCell.getDateCellValue();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = timeFormat.format(date);
        return formattedTime;
    }

    public static String generateRandomCode() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePart = dateFormat.format(new Date());

        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000);
        return datePart + randomNumber;
    }


    public void processTransTime(GeneralRequest request) {
        String updatedVersion = MessageConstant.ONE_VALUE;
        try {
            List<String> transTimes = new ArrayList<>();
            List<String> transTimePos = posRepository.getListTransTime(request);
            List<String> transTimeAgg = detailPaymentAggregatorRepository.getListTransTime(request);

            if (transTimePos.size() >= transTimeAgg.size()) {
                transTimes.addAll(transTimePos);
            } else {
                transTimes.addAll(transTimeAgg);
            }

            List<String> pmIds =  paymentMethodRepository.getPaymentMethods();
            for(String pmId : pmIds){
                request.setPmId(pmId);
                for(String transTime : transTimes){
                    request.setTransTime(transTime);
                    List<Long> detailAgg = detailPaymentAggregatorRepository.getDetailIdByRequestByBranch(request);
                    List<Long> detailPos = posRepository.getDetailPosIdByBranchAndTransTime(request);
                    System.out.println("leng pos "+ detailPos.size());
                    System.out.println("leng agg "+ detailAgg.size());
                    if(detailPos.size()!=0 && detailAgg.size()!=0){

                        if (detailPos.size() > detailAgg.size()) {
                            int indexPos = 0;
                            for(Long detailAggStr : detailAgg ){
                                detailPaymentAggregatorRepository.updateData(detailAggStr, updatedVersion);
                                posRepository.updateDataPos(detailAggStr, updatedVersion, detailPos.get(indexPos));
                                indexPos++;
                            }
                        } else if(detailPos.size()<detailAgg.size()){
                            int indexAgg = 0;
                            for(Long detailPosString : detailPos ){
                                detailPaymentAggregatorRepository.updateData(detailAgg.get(indexAgg), updatedVersion);
                                posRepository.updateDataPos(detailAgg.get(indexAgg), updatedVersion, detailPosString);
                                indexAgg++;
                            }
                        }else{
                            int indexPos = 0;
                            for(Long detailAggStr : detailAgg ){
                                detailPaymentAggregatorRepository.updateData(detailAggStr, updatedVersion);
                                posRepository.updateDataPos(detailAggStr, updatedVersion, detailPos.get(indexPos));
                                indexPos++;
                            }
                        }
                    }


                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Transactional
    public void reconBankAggregator(GeneralRequest request) {

        // get pm id by date
        List<String> pmIds = headerPaymentRepository.getPaymentMethodByDate(request.getTransDate());
        for(String pmId : pmIds){

            request.setPmId(pmId);
            int countDataBank = bankMutationRepository.getCountBank(request);
            List<BigDecimal> netAmountBank = bankMutationRepository.getAmontBank(request);

            int countDataAgg = detailPaymentAggregatorRepository.getCountDataAggByDate(request, netAmountBank);
            if(countDataAgg>0 && countDataBank>0){
                if(countDataAgg==countDataBank){
                    // update data agg
                    detailPaymentAggregatorRepository.updateDataReconBank(request, netAmountBank);
                }
            }
        }


    }


    public Response saveDataLog(GeneralRequest request){
        BaseResponse baseResponse;
        try {
            LogRecon logRecon = new LogRecon();
            logRecon.setBranchId(request.getBranchId());
            logRecon.setSubmittedAt(request.getTransDate());
//            logRecon.setSubmittedOn(request.getTransTime());
            logReconRepository.persist(logRecon);
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
