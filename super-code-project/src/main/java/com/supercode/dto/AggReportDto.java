package com.supercode.dto;

import java.math.BigDecimal;
import java.util.List;

public class AggReportDto {
    private String branch;
    private String transDate;
    private String transTime;
    private String transId;
    private String paymentMethod;
    private BigDecimal amountPos;
    private String inAggregator;
    private String statusInBank;

    public AggReportDto(String branch, String transDate, String transTime, String transId,
                        String paymentMethod, BigDecimal amountPos, String inAggregator, String statusInBank) {
        this.branch = branch;
        this.transDate = transDate;
        this.transTime = transTime;
        this.transId = transId;
        this.paymentMethod = paymentMethod;
        this.amountPos = amountPos;
        this.inAggregator = inAggregator;
        this.statusInBank = statusInBank;
    }

    // Getters & Setters (boleh pakai Lombok @Data juga)


    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmountPos() {
        return amountPos;
    }

    public void setAmountPos(BigDecimal amountPos) {
        this.amountPos = amountPos;
    }

    public String isInAggregator() {
        return inAggregator;
    }

    public void setInAggregator(String inAggregator) {
        this.inAggregator = inAggregator;
    }

    public String isStatusInBank() {
        return statusInBank;
    }

    public void setStatusInBank(String statusInBank) {
        this.statusInBank = statusInBank;
    }



    public static class PosReport{
        private List<AggReportDto> postReportDtos;
        private int totalData;
        private BigDecimal totalPos;
        private BigDecimal totalAgg;
        private BigDecimal diff;

        public List<AggReportDto> getPostReportDtos() {
            return postReportDtos;
        }

        public void setPostReportDtos(List<AggReportDto> postReportDtos) {
            this.postReportDtos = postReportDtos;
        }

        public int getTotalData() {
            return totalData;
        }

        public void setTotalData(int totalData) {
            this.totalData = totalData;
        }

        public BigDecimal getTotalPos() {
            return totalPos;
        }

        public void setTotalPos(BigDecimal totalPos) {
            this.totalPos = totalPos;
        }

        public BigDecimal getTotalAgg() {
            return totalAgg;
        }

        public void setTotalAgg(BigDecimal totalAgg) {
            this.totalAgg = totalAgg;
        }

        public BigDecimal getDiff() {
            return diff;
        }

        public void setDiff(BigDecimal diff) {
            this.diff = diff;
        }
    }
}

