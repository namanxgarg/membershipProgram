package com.firstclub.membership.domain;

import java.time.LocalDateTime;

// DTO for payment result
public class PaymentResult {
    private Boolean success;
    private String transactionId;
    private String errorMessage;
    private LocalDateTime processedAt;
    
    public PaymentResult(Boolean success, String transactionId) {
        this.success = success;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
    }
    
    public PaymentResult(Boolean success, String transactionId, String errorMessage) {
        this.success = success;
        this.transactionId = transactionId;
        this.errorMessage = errorMessage;
        this.processedAt = LocalDateTime.now();
    }
    
    // Getters
    public Boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getProcessedAt() { return processedAt; }
}
