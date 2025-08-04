package com.supercode.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.supercode.entity.BankMutation;
import com.supercode.entity.DebitCredit;
import com.supercode.repository.BankMutationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Transactional
    public void saveDetailBank(MultipartFormDataInput file, String pmId, String branchId, String parentId, String transDate, String user) {
        try {
            InputPart inputPart = generalService.getInputPart(file);
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String fileName = getFileName(headers);

            try (InputStream inputStream = inputPart.getBody(InputStream.class, null)) {
                String fileExtension = getFileExtension(fileName);

                if (fileExtension.equalsIgnoreCase("csv")) {
                    processCSV(inputStream, pmId, parentId, transDate, user);
                } else if (fileExtension.equalsIgnoreCase("xlsx") || fileExtension.equalsIgnoreCase("xls")) {
                    processExcel(inputStream, pmId, parentId, transDate, user);
                } else {
                    throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processCSV(InputStream inputStream, String pmId, String parentId, String transDate, String user) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String[] nextLine;
            boolean isFirstRow = true;
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstRow) { // Skip header
                    isFirstRow = false;
                    continue;
                }
                processRow(nextLine, pmId, parentId, transDate, user);
            }
        }
    }

    private void processExcel(InputStream inputStream, String pmId, String parentId, String transDate, String user) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                processRow(row, pmId, parentId,transDate, user);
            }
        }
    }

    private void processRow(Object row, String pmId, String parentId, String transDate, String user) {
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

            if(!formattedTimeDate.equalsIgnoreCase(transDate)){
                return;
            }
            BankMutation bm = new BankMutation();
            bm.setBank(pmName);
            bm.setAccountNo(accountNo);
            bm.setNotes(notes);
            bm.setAmount(grossAmount);
            bm.setDebitCredit(DebitCredit.valueOf(debitCredit));
            bm.setTransDate(java.sql.Date.valueOf(formattedTimeDate));
            bm.setParentId(parentId);
            bm.setCreatedBy(user);
            bankMutationRepository.persist(bm);
//            headerPaymentRepository.updateDate(parentId, formattedTimeDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> "";
        };
    }

    private double getNumericCellValue(Cell cell) {
        try {
            if (cell == null) return 0.0;

            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();

                case STRING:
                    String raw = cell.getStringCellValue();
                    // Hapus "CR", "DB", koma, dan spasi
                    String cleaned = raw.replace("CR", "")
                            .replace("DB", "")
                            .replace(",", "")
                            .replace(" ", "")
                            .trim();

                    return Double.parseDouble(cleaned);

                default:
                    return 0.0;
            }

        } catch (Exception e) {
            System.err.println("Gagal parsing cell ke double: " + e.getMessage());
            return 0.0;
        }
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

    public void saveDetailBankBca(MultipartFormDataInput file, String pmId, String branchId, String parentId, String transDate, String user) {
        try {
            InputPart inputPart = generalService.getInputPart(file);
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String fileName = getFileName(headers);

            try (InputStream inputStream = inputPart.getBody(InputStream.class, null)) {
                String fileExtension = getFileExtension(fileName);

                if (fileExtension.equalsIgnoreCase("csv")) {
                    processCSVBca(inputStream, pmId, parentId, transDate, user);
                } else if (fileExtension.equalsIgnoreCase("xlsx") || fileExtension.equalsIgnoreCase("xls")) {
                    processExcelBca(inputStream, pmId, parentId, transDate, user);
                } else {
                    throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processExcelBca(InputStream inputStream, String pmId, String parentId, String transDate, String user) throws IOException {
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

                Cell dateCell = row.getCell(0);
                if (dateCell == null) break;

                // Cek apakah cell 0 adalah tanggal yang valid (bisa tipe NUMERIC atau STRING dengan format tertentu)
                boolean validDate = false;
                if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                    validDate = true;
                } else if (dateCell.getCellType() == CellType.STRING) {
                    String val = dateCell.getStringCellValue().trim();
                    validDate = val.matches("\\d{1,2}/\\d{1,2}"); // contoh: 29/04
                }

                if (!validDate) {
                    // Keluar dari loop karena sudah tidak sesuai format
                    break;
                }

                processRowBca(row, pmId, parentId, accountNo, transDate, user);
            }
        }
    }

        private void processRowBca(Row row, String pmId, String parentId, String accountNo, String transDate, String user) {
            try {
                String notes = getCellValue(row.getCell(1));
                String formattedTimeDate = getFormattedDate(row.getCell(0), transDate);
                if(!formattedTimeDate.equalsIgnoreCase(transDate)){
                    return;
                }
                double creditAmount = getNumericCellValue(row.getCell(3));
                BigDecimal grossAmount = new BigDecimal(creditAmount);
                String debitCredit = creditAmount > 0 ? "Credit" : "Debit";
                if(row.getCell(3).toString().contains("DB")){
                    debitCredit = "Debit";
                }

                String pmName = paymentMethodRepository.getPaymentMethodByPmId(pmId);

                BankMutation bm = new BankMutation();
                bm.setBank(pmName);
                bm.setAccountNo(accountNo); // Menggunakan nomor rekening dari header
                bm.setNotes(notes);
                bm.setAmount(grossAmount);
                bm.setDebitCredit(DebitCredit.valueOf(debitCredit));
                bm.setTransDate(java.sql.Date.valueOf(formattedTimeDate));
                bm.setParentId(parentId);
                bm.setCreatedBy(user);
                bankMutationRepository.persist(bm);
//                headerPaymentRepository.updateDate(parentId, formattedTimeDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




    String getFormattedDate(Cell dateCell, String transDate) {
        try {
            if (dateCell.getCellType() == CellType.NUMERIC) {
                Date date = dateCell.getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(date);
            } else if (dateCell.getCellType() == CellType.STRING) {
                String rawDate = dateCell.getStringCellValue().trim(); // contoh: "29/04"

                // Ambil hanya tahun dari transDate (misalnya: "2025-04-29")
                String year = transDate.substring(0, 4); // hasil: "2025"

                // Gabungkan jadi "29/04/2025"
                String combinedDate = rawDate + "/" + year;

                // Parsing dan format
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = inputFormat.parse(combinedDate);
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public void processCSVBca(InputStream inputStream, String pmId, String parentId, String transDate, String user) throws IOException, CsvValidationException {
        List<String[]> csvData = new ArrayList<>();
        String accountNo = "";
        String period = "";
        int transactionStartIndex = -1;

        System.out.println("Mulai membaca file CSV BCA...");

        // Menggunakan CSVReader dari OpenCSV
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String[] row;
            int rowNumber = 0;
            while ((row = reader.readNext()) != null) {
                System.out.println("Panjang row: " + row.length);
                rowNumber++;

// Ambil info rekening dan periode
                if (row.length > 0 && row[0].startsWith("No. rekening :")) {
                    accountNo = row[0].replace("No. rekening :", "").trim();
                    System.out.println("Nomor rekening ditemukan: " + accountNo);
                    continue;
                }

                if (row.length > 0 && row[0].startsWith("Periode :")) {
                    String[] dateParts = row[0].replace("Periode :", "").trim().split(" - ");
                    if (dateParts.length > 0) {
                        period = dateParts[0].split("/")[2];
                    }
                    continue;
                }

// Setelah header
                if (rowNumber > 6) {
                    String[] cleanedRow = Arrays.stream(row)
                            .map(cell -> cell.replace("\"", "").trim())
                            .toArray(String[]::new);

                    if (cleanedRow[0].equalsIgnoreCase("Tanggal Transaksi")) {
                        transactionStartIndex = rowNumber;
                        System.out.println("Header transaksi ditemukan di baris: " + rowNumber);
                        continue;
                    }

                    if (cleanedRow[0].isEmpty()) continue;

                    // Format ulang tanggal
                    try {
                        String dateStr = cleanedRow[0] + "/" + period;
                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
                        Date date = inputFormat.parse(dateStr);
                        cleanedRow[0] = outputFormat.format(date);
                    } catch (ParseException e) {
                        System.err.println("Gagal parsing tanggal: " + cleanedRow[0]);
                        continue;
                    }

                    System.out.println("Row transaksi valid, jumlah kolom: " + cleanedRow.length);
                    csvData.add(cleanedRow);
                }
            }
        }

        // Validasi
        if (accountNo.isEmpty()) {
            System.err.println("Nomor rekening tidak ditemukan!");
            return;
        }

        if (transactionStartIndex == -1) {
            System.err.println("Header transaksi tidak ditemukan!");
            return;
        }

        // Proses transaksi

        for (int i = 1; i < csvData.size(); i++) {
            String[] row = csvData.get(i);
            processRowCSVBca(row, pmId, parentId, accountNo, transDate, user);
        }
    }

    /*public void processCSVBca(InputStream inputStream, String pmId, String parentId, String transDate) throws IOException, CsvValidationException {
        List<String[]> csvData = new ArrayList<>();
        String accountNo = "";
        String period = "";
        boolean startReading = false;

        System.out.println("Mulai membaca file CSV BCA...");

        // Gunakan delimiter ; karena kemungkinan file dari Excel regional Indonesia
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withQuoteChar('"')
                .build();

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .withCSVParser(parser)
                .build()) {

            String[] row;
            int rowNumber = 0;
            while ((row = reader.readNext()) != null) {
                rowNumber++;
                if (row.length == 0 || row[0].isEmpty()) continue;

                String firstCell = row[0].trim();

                // Info rekening
                if (firstCell.startsWith("No. rekening :")) {
                    accountNo = firstCell.replace("No. rekening :", "").trim();
                    System.out.println("Nomor rekening ditemukan: " + accountNo);
                    continue;
                }

                // Info periode
                if (firstCell.startsWith("Periode :")) {
                    String[] dateParts = firstCell.replace("Periode :", "").trim().split(" - ");
                    if (dateParts.length > 0) {
                        String[] startDateParts = dateParts[0].split("/");
                        if (startDateParts.length == 3) {
                            period = startDateParts[2];  // Ambil tahun
                        }
                    }
                    continue;
                }

                // Deteksi awal transaksi
                if (firstCell.equalsIgnoreCase("Tanggal Transaksi")) {
                    startReading = true;
                    System.out.println("Header transaksi ditemukan di baris: " + rowNumber);
                    continue;
                }

                // Lewati sebelum transaksi
                if (!startReading) continue;

                // Proses baris transaksi
                if (row.length < 5) {
                    System.err.println("Baris transaksi tidak valid di baris " + rowNumber + ": kolom kurang");
                    continue;
                }

                try {
                    // Format ulang tanggal
                    String dateStr = row[0].trim() + "/" + period;
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
                    Date date = inputFormat.parse(dateStr);
                    row[0] = outputFormat.format(date);
                } catch (ParseException e) {
                    System.err.println("Gagal parsing tanggal di baris " + rowNumber + ": " + row[0]);
                    continue;
                }

                csvData.add(row);
            }
        }

        // Validasi akhir
        if (accountNo.isEmpty()) {
            System.err.println("Nomor rekening tidak ditemukan!");
            return;
        }

        if (!startReading) {
            System.err.println("Header transaksi tidak ditemukan!");
            return;
        }

        // Proses transaksi
        for (String[] row : csvData) {
            processRowCSVBca(row, pmId, parentId, accountNo, transDate);
        }
    }*/





    private void processRowCSVBca(String[] row, String pmId, String parentId, String accountNo, String transDate, String user) {
        try {
            System.out.println("ini row "+ row.length);
            if (row.length == 1) {
                // Misalnya, jika CSV menggunakan koma sebagai delimiter, kita dapat memecahnya secara manual
                String[] splitRow = row[0].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // Memisahkan koma, tapi menjaga data yang ada dalam tanda kutip

                // Debugging: Tampilkan hasil pemisahan
                System.out.println("Hasil pemisahan: " + String.join(" | ", splitRow));

                // Set ulang row dengan hasil pemisahan
                row = splitRow;
            }
            System.out.println("masuk sini tak "+ row[0]);

//            String formattedTimeDate = row[0].trim();
            String formattedTimeDate = row[0].trim();
            String notes = row[1].trim();
            double creditAmount = parseAmount(row[3]);
            BigDecimal grossAmount = BigDecimal.valueOf(creditAmount);
            String debitCredit = (creditAmount > 0) ? "Credit" : "Debit";
            String pmName = "BANK BCA";

            BankMutation bm = new BankMutation();
            bm.setBank(pmName);
            bm.setAccountNo(accountNo);
            bm.setNotes(notes);
            bm.setAmount(grossAmount);
            bm.setDebitCredit(DebitCredit.valueOf(debitCredit));
            bm.setCreatedBy(user);
            String parsedDate = parseDate(formattedTimeDate);
            if(parsedDate.equalsIgnoreCase(transDate)){
                bm.setTransDate(java.sql.Date.valueOf(parsedDate));
                bm.setParentId(parentId);
                bankMutationRepository.persist(bm);
                System.out.println("Data berhasil diproses: " + bm);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseDate(String dateStr) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd"); // Format respons

        Date date = inputFormat.parse(dateStr);
        return outputFormat.format(date);
    }

    private double parseAmount(String amount) {
        try {
            amount = amount.replaceAll("[^0-9.,]", "").trim();
            amount = amount.replace(",", "");
            return Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            System.err.println("Gagal parsing jumlah: " + amount);
            return 0;
        }
    }



    private char detectDelimiter(BufferedReader br) throws IOException {
        br.mark(1000);
        String line = br.readLine();
        br.reset();

        if (line.contains(";")) {
            return ';';
        } else {
            return ',';
        }
    }




}

