package com.supercode.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection(targets = {BaseResponse.class}) // Tambahkan ini
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse implements Serializable {

    public Integer result;
    public String message;
    public String error;
    public Object payload;

    // Konstruktor
    public BaseResponse(Integer result, String message) {
        this.result = result;
        this.message = message;
    }
    public BaseResponse(){}

    public BaseResponse(Integer result, String message, String error, Object payload) {
        this.result = result;
        this.message = message;
        this.error = error;
        this.payload = payload;
    }

    // Getter wajib agar bisa dikonversi ke JSON
    public Integer getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public Object getPayload() {
        return payload;
    }
}
