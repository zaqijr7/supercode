package com.supercode.entity;

import com.supercode.util.MessageConstant;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "detail_agregator_payment")
public class DetailPaymentAggregator extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_payment_id", nullable = false, updatable = false)
    private Long detailPaymentId;

    @Column(name = "payment_id",  length = 50)
    private String paymentId;

    @Column(name = "pm_id", nullable = false, length = 50)
    private String pmId;

    @Column(name = "branch_id", nullable = false, length = 50)
    private String branchId;

    @Column(name = "trans_date")
    private String transDate;

    @Column(name = "trans_time", nullable = false)
    private String transTime;

    @Column(name = "trans_id", nullable = false, length = 50)
    private String transId;

    @Column(name = "gross_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "net_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "charge", nullable = false)
    private BigDecimal charge = BigDecimal.ZERO;


    @Column(name = "settlement_date")
    private String settlementDate;

    @Column(name = "settlement_time", nullable = false)
    private String settlementTime;

    @Column(name = "flag_rekon_pos", nullable = false)
    private Boolean flagRekonPos;

    @Column(name = "flag_rekon_bank", nullable = false)
    private Boolean flagRekonBank;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "changed_by", length = 50)
    private String changedBy;

    @Column(name = "changed_at")
    private Timestamp changedAt;

    @Column(name = "changed_on")
    private Date changedOn;

    @Column(name = "notes", length = 150)
    private String notes;

    @Column(name = "parent_id", length = 50)
    private String parentId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        createdBy= MessageConstant.SYSTEM;
        createdOn=LocalDateTime.now();
        flagRekonBank=MessageConstant.FALSE_VALUE;
        flagRekonPos=MessageConstant.FALSE_VALUE;
    }

    // Getters and Setters
    public Long getDetailPaymentId() {
        return detailPaymentId;
    }

    public void setDetailPaymentId(Long detailPaymentId) {
        this.detailPaymentId = detailPaymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
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

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }

    public Boolean getFlagRekonPos() {
        return flagRekonPos;
    }

    public void setFlagRekonPos(Boolean flagRekonPos) {
        this.flagRekonPos = flagRekonPos;
    }

    public Boolean getFlagRekonBank() {
        return flagRekonBank;
    }

    public void setFlagRekonBank(Boolean flagRekonBank) {
        this.flagRekonBank = flagRekonBank;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
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

    public Timestamp getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Timestamp changedAt) {
        this.changedAt = changedAt;
    }

    public Date getChangedOn() {
        return changedOn;
    }

    public void setChangedOn(Date changedOn) {
        this.changedOn = changedOn;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}

