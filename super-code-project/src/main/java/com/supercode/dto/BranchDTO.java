package com.supercode.dto;

public class BranchDTO {
    private String branchId;
    private String branchName;

    public BranchDTO(String branchId, String branchName) {
        this.branchId = branchId;
        this.branchName = branchName;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getBranchName() {
        return branchName;
    }
}
