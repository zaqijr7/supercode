package com.supercode.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "master_merchant")
public class MasterMerchant {

    @Id
    @Column(name = "branch_id", nullable = false, length = 50)
    private String branchId;

    @Column(name = "branch_name", nullable = false, length = 100)
    private String branchName;

    @Column(name = "shopee_store_id", length = 50)
    private String shopeeStoreId;

    @Column(name = "shopee_store_name", length = 100)
    private String shopeeStoreName;

    @Column(name = "grabfood_store_id", length = 50)
    private String grabfoodStoreId;

    @Column(name = "goto_store_id", length = 50)
    private String gotoStoreId;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy = "SYSTEM";

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;

    @Column(name = "status", nullable = false)
    private boolean status = true;

    // Getters and Setters
    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getShopeeStoreId() {
        return shopeeStoreId;
    }

    public void setShopeeStoreId(String shopeeStoreId) {
        this.shopeeStoreId = shopeeStoreId;
    }

    public String getShopeeStoreName() {
        return shopeeStoreName;
    }

    public void setShopeeStoreName(String shopeeStoreName) {
        this.shopeeStoreName = shopeeStoreName;
    }

    public String getGrabfoodStoreId() {
        return grabfoodStoreId;
    }

    public void setGrabfoodStoreId(String grabfoodStoreId) {
        this.grabfoodStoreId = grabfoodStoreId;
    }

    public String getGotoStoreId() {
        return gotoStoreId;
    }

    public void setGotoStoreId(String gotoStoreId) {
        this.gotoStoreId = gotoStoreId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

