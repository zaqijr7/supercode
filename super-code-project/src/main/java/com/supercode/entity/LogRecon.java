package com.supercode.entity;

import com.supercode.util.MessageConstant;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="log_process_recon")
public class LogRecon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false, updatable = false)
    private Long logId;

    @Column(name = "branch_id", length = 50, nullable = false)
    private String branchId;

    @Column(name = "date", length = 50, nullable = false)
    private String date;


    @Column(name = "submitted_at", nullable = false)
    @Temporal(TemporalType.TIME)
    private String submittedAt;

    @Column(name = "submitted_by", length = 50)
    private String submittedBy;

    @Column(name = "submitted_on")
    private String submittedOn;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

//    @Column(name = "created_on", nullable = false)
    private String createdOn;

    @Column(name = "changed_by", length = 50)
    private String changedBy;

    @Column(name = "changed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private String changedAt;

    @Column(name = "changed_on")
    private String changedOn;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public java.lang.String getBranchId() {
        return branchId;
    }

    public void setBranchId(java.lang.String branchId) {
        this.branchId = branchId;
    }



    public java.lang.String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(java.lang.String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public java.lang.String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(java.lang.String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public java.lang.String getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(java.lang.String submittedOn) {
        this.submittedOn = submittedOn;
    }

    public java.lang.String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(java.lang.String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public java.lang.String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(java.lang.String createdOn) {
        this.createdOn = createdOn;
    }

    public java.lang.String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(java.lang.String changedBy) {
        this.changedBy = changedBy;
    }

    public java.lang.String getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(java.lang.String changedAt) {
        this.changedAt = changedAt;
    }

    public java.lang.String getChangedOn() {
        return changedOn;
    }

    public void setChangedOn(java.lang.String changedOn) {
        this.changedOn = changedOn;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        createdBy= MessageConstant.SYSTEM;
//        createdOn= "0000:00:00";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
