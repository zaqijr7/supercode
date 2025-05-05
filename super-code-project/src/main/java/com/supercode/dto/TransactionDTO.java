package com.supercode.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {
    private String transactionDate;
    private String branchId;
    private Boolean statusRecon;
    private List<TransactionList> transactionList;
    private Integer submitStatus;
    private BigDecimal allGross;
    private BigDecimal allNet;

    public BigDecimal getAllGross() {
        return allGross;
    }

    public void setAllGross(BigDecimal allGross) {
        this.allGross = allGross;
    }

    public BigDecimal getAllNet() {
        return allNet;
    }

    public void setAllNet(BigDecimal allNet) {
        this.allNet = allNet;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public Boolean isStatusRecon() {
        return statusRecon;
    }

    public void setStatusRecon(Boolean statusRecon) {
        this.statusRecon = statusRecon;
    }

    public List<TransactionList> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<TransactionList> transactionList) {
        this.transactionList = transactionList;
    }

    public Integer isSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TransactionList{
        private String  transactionSource;
        private BigDecimal amount;
        Boolean statusRecon;
        private String paymentId;
        String createdAt;
        private String fileName;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getTransactionSource() {
            return transactionSource;
        }

        public void setTransactionSource(String transactionSource) {
            this.transactionSource = transactionSource;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public Boolean isStatusRecon() {
            return statusRecon;
        }

        public void setStatusRecon(Boolean statusRecon) {
            this.statusRecon = statusRecon;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(String paymentId) {
            this.paymentId = paymentId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}
