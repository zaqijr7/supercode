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
}
