package com.supercode.entity;

import com.supercode.util.MessageConstant;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Entity
@Table(name = "detail_point_of_sales")
public class DetailPaymentPos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_pos_id", nullable = false, updatable = false)
    private Long detailPosId;

    @Column(name = "pm_id", nullable = false, length = 50)
    private String pmId;

    @Column(name = "branch_id", nullable = false, length = 50)
    private String branchId;

    @Column(name = "trans_date", nullable = false)
    private String transDate;

    @Column(name = "trans_time", nullable = false)
    private String transTime;

    @Column(name = "trans_id", nullable = false, length = 50)
    private String transId;

    @Column(name = "gross_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "flag_rekon_ecom", nullable = false)
    private Boolean flagRekonEcom;

    @Column(name = "flag_manual", nullable = false)
    private Boolean flagManual;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "created_on", nullable = false)
    private LocalDate createdOn;

    @Column(name = "changed_by", length = 50)
    private String changedBy;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "changed_on")
    private LocalDate changedOn;

    @Column(name = "pay_method_aggregator", length = 50)
    private String payMethodAggregator;

    @Column(name = "parent_id", length = 50)
    private String parentId;

    @Column(name = "detail_id_agg", length = 50)
    private String detailIdAggregator;

    @PrePersist
    protected void onCreate() {
        LocalDateTime createdAts = LocalDateTime.now();
        String timeOnly = createdAts.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        createdAt = timeOnly;
        createdOn=LocalDate.now();
        flagManual=MessageConstant.FALSE_VALUE;
        flagRekonEcom=MessageConstant.FALSE_VALUE;
    }

    public Long getDetailPosId() {
        return detailPosId;
    }

    public void setDetailPosId(Long detailPosId) {
        this.detailPosId = detailPosId;
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

    public Boolean getFlagRekonEcom() {
        return flagRekonEcom;
    }

    public void setFlagRekonEcom(Boolean flagRekonEcom) {
        this.flagRekonEcom = flagRekonEcom;
    }

    public Boolean getFlagManual() {
        return flagManual;
    }

    public void setFlagManual(Boolean flagManual) {
        this.flagManual = flagManual;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public LocalDate getChangedOn() {
        return changedOn;
    }

    public void setChangedOn(LocalDate changedOn) {
        this.changedOn = changedOn;
    }

    public String getPayMethodAggregator() {
        return payMethodAggregator;
    }

    public void setPayMethodAggregator(String payMethodAggregator) {
        this.payMethodAggregator = payMethodAggregator;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDetailIdAggregator() {
        return detailIdAggregator;
    }

    public void setDetailIdAggregator(String detailIdAggregator) {
        this.detailIdAggregator = detailIdAggregator;
    }
}
