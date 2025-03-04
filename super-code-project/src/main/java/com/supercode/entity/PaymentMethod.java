package com.supercode.entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "payment_method")
public class PaymentMethod {
    @Id
    @Column(name = "pm_id", nullable = false, length = 50)
    private String pmId;

    @Column(name = "payment_method", nullable = false, length = 100)
    private String paymentMethod;

    @Column(name = "payment_type", nullable = false, length = 50)
    private String paymentType;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy = "SYSTEM";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "bank_disburse", length = 50)
    private String bankDisburse;

    @Column(name = "bank_acc_no", length = 50)
    private String bankAccNo;

    @Column(name = "disburse_day", length = 50)
    private String disburseDay;

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getBankDisburse() {
        return bankDisburse;
    }

    public void setBankDisburse(String bankDisburse) {
        this.bankDisburse = bankDisburse;
    }

    public String getBankAccNo() {
        return bankAccNo;
    }

    public void setBankAccNo(String bankAccNo) {
        this.bankAccNo = bankAccNo;
    }

    public String getDisburseDay() {
        return disburseDay;
    }

    public void setDisburseDay(String disburseDay) {
        this.disburseDay = disburseDay;
    }
}
