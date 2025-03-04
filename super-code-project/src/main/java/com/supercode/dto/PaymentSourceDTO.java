package com.supercode.dto;

public class PaymentSourceDTO {
    private String paymentName;
    private String paymentMethodId;

    public PaymentSourceDTO(String paymentName, String paymentMethodId){
        this.paymentName=paymentName;
        this.paymentMethodId=paymentMethodId;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
