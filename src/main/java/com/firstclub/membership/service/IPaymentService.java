package com.firstclub.membership.service;

import com.firstclub.membership.domain.PaymentResult;
import java.math.BigDecimal;

public interface IPaymentService {
    /**
     * Process a payment for membership renewal
     * 
     * @param userId User ID making the payment
     * @param amount Amount to charge
     * @param description Description of the payment
     * @return PaymentResult with success status and details
     */
    PaymentResult processPayment(String userId, BigDecimal amount, String description);
}
