package com.supercode.dto;

import java.util.List;

public class PaymentDTO {
    private String paymentType;
    List<PaymentSourceDTO> paymentSources;

    public PaymentDTO(){

    }

    public PaymentDTO(String key, List<PaymentSourceDTO> value) {
        this.paymentType=key;
        this.paymentSources= value;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<PaymentSourceDTO> getPaymentSources() {
        return paymentSources;
    }

    public void setPaymentSources(List<PaymentSourceDTO> paymentSources) {
        this.paymentSources = paymentSources;
    }
}
