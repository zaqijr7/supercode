package com.supercode.service;

import com.supercode.entity.BankMutation;
import com.supercode.entity.DebitCredit;
import com.supercode.repository.BankMutationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.ws.rs.core.MultivaluedMap;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import com.supercode.repository.PaymentMethodRepository;
import com.supercode.repository.HeaderPaymentRepository;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BankMutationService {

    @Inject
    GeneralService generalService;

    @Inject
    PaymentMethodRepository paymentMethodRepository;

    @Inject
    BankMutationRepository bankMutationRepository;

    @Inject
    HeaderPaymentRepository headerPaymentRepository;

    public void saveDetailBank(MultipartFormDataInput file, String pmId, String branchId, String parentId) {
        try {
            InputPart inputPart = generalService.getInputPart(file);
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String fileName = getFileName(headers);

            try (InputStream inputStream = inputPart.getBody(InputStream.class, null)) {
                String fileExtension = getFileExtension(fileName);

                if (fileExtension.equalsIgnoreCase("csv")) {
                    processCSV(inputStream, pmId, parentId);
                } else if (fileExtension.equalsIgnoreCase("xlsx") || fileExtension.equalsIgnoreCase("xls")) {
                    processExcel(inputStream, pmId, parentId);
                } else {
                    throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processCSV(InputStream inputStream, String pmId, String parentId) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String[] nextLine;
            boolean isFirstRow = true;
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstRow) { // Skip header
                    isFirstRow = false;
                    continue;
                }
                processRow(nextLine, pmId, parentId);
            }
        }
    }

    private void processExcel(InputStream inputStream, String pmId, String parentId) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                processRow(row, pmId, parentId);
            }
        }
    }

    private void processRow(Object row, String pmId, String parentId) {
        try {
            String accountNo;
            String notes;
            String formattedTimeDate;
            double creditAmount = 0;
            BigDecimal grossAmount = null;

            if (row instanceof Row) { // Jika Excel
                Row excelRow = (Row) row;
                accountNo = getCellValue(excelRow.getCell(0));
                notes = getCellValue(excelRow.getCell(5));
                formattedTimeDate = generalService.getFormattedDate(excelRow.getCell(2));
                creditAmount = getNumericCellValue(excelRow.getCell(8));
                grossAmount = new BigDecimal(creditAmount);
            } else { // Jika CSV
                String[] csvRow = (String[]) row;
                accountNo = csvRow[0];
                notes = csvRow[5];
                formattedTimeDate = csvRow[2];
                creditAmount = Double.parseDouble(csvRow[8].replace(",", ""));
                grossAmount = new BigDecimal(creditAmount);
            }

            String debitCredit = creditAmount > 0 ? "Credit" : "Debit";
            String pmName = paymentMethodRepository.getPaymentMethodByPmId(pmId);

            BankMutation bm = new BankMutation();
            bm.setBank(pmName);
            bm.setAccountNo(accountNo);
            bm.setNotes(notes);
            bm.setAmount(grossAmount);
            bm.setDebitCredit(DebitCredit.valueOf(debitCredit));
            bm.setTransDate(formattedTimeDate);

            bankMutationRepository.persist(bm);
            headerPaymentRepository.updateDate(parentId, formattedTimeDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> "";
        };
    }

    private double getNumericCellValue(Cell cell) {
        if (cell == null) return 0;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> Double.parseDouble(cell.getStringCellValue().replace(",", ""));
            default -> 0;
        };
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "";
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }

    private String getFileName(MultivaluedMap<String, String> headers) {
        String contentDisposition = headers.getFirst("Content-Disposition");
        if (contentDisposition == null) return "";
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf("=") + 1).trim().replace("\"", "");
            }
        }
        return "";
    }

    public void saveDetailBankBca(MultipartFormDataInput file, String pmId, String branchId, String parentId) {
        try {
            InputPart inputPart = generalService.getInputPart(file);
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String fileName = getFileName(headers);

            try (InputStream inputStream = inputPart.getBody(InputStream.class, null)) {
                String fileExtension = getFileExtension(fileName);

                if (fileExtension.equalsIgnoreCase("csv")) {
                    processCSVBca(inputStream, pmId, parentId);
                } else if (fileExtension.equalsIgnoreCase("xlsx") || fileExtension.equalsIgnoreCase("xls")) {
                    processExcelBca(inputStream, pmId, parentId);
                } else {
                    throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processExcelBca(InputStream inputStream, String pmId, String parentId) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            String accountNo = "";

            // Menangkap nomor rekening dari header (asumsi di baris 1 atau 2)
            for (int i = 0; i < 5; i++) { // Cek di beberapa baris awal
                Row row = sheet.getRow(i);
                if (row != null) {
                    for (Cell cell : row) {
                        String cellValue = getCellValue(cell);
                        if (cellValue.replaceAll("[^0-9]", "").length() >= 10) { // Jika mengandung 10+ digit
                            accountNo = cellValue.replaceAll("[^0-9]", ""); // Ambil hanya angka
                            break;
                        }
                    }
                }
                if (!accountNo.isEmpty()) break;
            }

            // Mulai membaca data transaksi dari baris ke-7 ke bawah (asumsi format BCA)
            for (Row row : sheet) {
                if (row.getRowNum() < 7) continue;
                processRowBca(row, pmId, parentId, accountNo);
            }
        }
    }

        private void processRowBca(Row row, String pmId, String parentId, String accountNo) {
            try {
                String notes = getCellValue(row.getCell(1));
                String formattedTimeDate = generalService.getFormattedDate(row.getCell(0));
                double creditAmount = getNumericCellValue(row.getCell(4));
                BigDecimal grossAmount = new BigDecimal(creditAmount);
                String debitCredit = creditAmount > 0 ? "Credit" : "Debit";
                String pmName = paymentMethodRepository.getPaymentMethodByPmId(pmId);

                BankMutation bm = new BankMutation();
                bm.setBank(pmName);
                bm.setAccountNo(accountNo); // Menggunakan nomor rekening dari header
                bm.setNotes(notes);
                bm.setAmount(grossAmount);
                bm.setDebitCredit(DebitCredit.valueOf(debitCredit));
                bm.setTransDate(formattedTimeDate);

                bankMutationRepository.persist(bm);
                headerPaymentRepository.updateDate(parentId, formattedTimeDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private void processCSVBca(InputStream inputStream, String pmId, String parentId) throws IOException, CsvValidationException{
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String[] nextLine;
            boolean isFirstRow = true;
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstRow) { // Skip header
                    isFirstRow = false;
                    continue;
                }
                processRow(nextLine, pmId, parentId);
            }
        }
    }
}

