package com.firstclub.membership.service;

import com.firstclub.membership.domain.PaymentResult;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MockPaymentService implements IPaymentService {
    private final Map<String, PaymentResult> paymentHistory = new HashMap<>();
    private final Random random = new Random();
    
    private Boolean simulateFailure = false;
    private String failureUserId = null;
    
    @Override
    public PaymentResult processPayment(String userId, BigDecimal amount, 
                                       String description) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (simulateFailure && (failureUserId == null || failureUserId.equals(userId))) {
            String transactionId = "txn_failed_" + System.currentTimeMillis();
            PaymentResult result = new PaymentResult(
                false, 
                transactionId, 
                "Insufficient funds or payment method declined"
            );
            paymentHistory.put(transactionId, result);
            return result;
        }
        
        String transactionId = "txn_" + System.currentTimeMillis() + "_" + 
                              random.nextInt(10000);
        PaymentResult result = new PaymentResult(true, transactionId);
        paymentHistory.put(transactionId, result);
        
        System.out.println("Payment processed: " + transactionId + 
                          " for user " + userId + 
                          " amount: $" + amount);
        
        return result;
    }
    public void setSimulateFailure(Boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
    
    public void setFailureUserId(String userId) {
        this.failureUserId = userId;
    }
    
    public PaymentResult getPaymentHistory(String transactionId) {
        return paymentHistory.get(transactionId);
    }
}
