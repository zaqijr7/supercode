package com.supercode.entity;

import com.supercode.util.MessageConstant;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Entity
@Table(name="header_payment")
public class HeaderPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, updatable = false)
    private Long paymentId;

    @Column(name = "trans_date")
    private String transDate;

    @Column(name = "pm_id")
    private String pmId;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "status_rekon_pos_vs_ecom", length = 50)
    private String statusRekonPosVsEcom;

    @Column(name = "status_rekon_ecom_vs_bank", length = 50)
    private String statusRekonEcomVsBank;

    @Column(name = "status_rekom_ecom_vs_pos", length = 50)
    private String statusRekonEcomVsPos;

    @Column(name = "created_by", length = 100, nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "changed_by", length = 100)
    private String changedBy;

    @Column(name = "changed_at")
    private String changedAt;

    @Column(name = "changed_on")
    private LocalDateTime changedOn;

    @Column(name = "parent_id", length = 50)
    private String parentId;

    @Column(name = "branch_id", length = 50)
    private String branchId;

    @PrePersist
    protected void onCreate() {
        LocalDateTime createdAts = LocalDateTime.now();
        String timeOnly = createdAts.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        createdAt = timeOnly;
        createdBy=MessageConstant.SYSTEM;
        createdOn=LocalDateTime.now();
        statusRekonEcomVsBank= MessageConstant.ZERO_VALUE;
        statusRekonPosVsEcom=MessageConstant.ZERO_VALUE;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }



    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatusRekonPosVsEcom() {
        return statusRekonPosVsEcom;
    }

    public void setStatusRekonPosVsEcom(String statusRekonPosVsEcom) {
        this.statusRekonPosVsEcom = statusRekonPosVsEcom;
    }

    public String getStatusRekonEcomVsBank() {
        return statusRekonEcomVsBank;
    }

    public void setStatusRekonEcomVsBank(String statusRekonEcomVsBank) {
        this.statusRekonEcomVsBank = statusRekonEcomVsBank;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(String changedAt) {
        this.changedAt = changedAt;
    }

    public LocalDateTime getChangedOn() {
        return changedOn;
    }

    public void setChangedOn(LocalDateTime changedOn) {
        this.changedOn = changedOn;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getStatusRekonEcomVsPos() {
        return statusRekonEcomVsPos;
    }

    public void setStatusRekonEcomVsPos(String statusRekonEcomVsPos) {
        this.statusRekonEcomVsPos = statusRekonEcomVsPos;
    }
}
