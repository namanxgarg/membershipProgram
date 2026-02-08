package com.firstclub.membership.manager;

import com.firstclub.membership.domain.*;
import com.firstclub.membership.repository.IMembershipRepository;
import com.firstclub.membership.service.IPaymentService;
import com.firstclub.membership.domain.PaymentResult;
import java.time.LocalDateTime;
import java.util.List;

public class RenewalManager {
    private IMembershipRepository membershipRepository;
    private TierManager tierManager;
    private IPaymentService paymentService;
    
    public RenewalManager(IMembershipRepository membershipRepository,
                         TierManager tierManager,
                         IPaymentService paymentService) {
        this.membershipRepository = membershipRepository;
        this.tierManager = tierManager;
        this.paymentService = paymentService;
    }
    
    public void processRenewals() {
        LocalDateTime today = LocalDateTime.now();
        List<UserMembership> expiring = 
            membershipRepository.findExpiringMemberships(today);
        
        for (UserMembership membership : expiring) {
            if (membership.getAutoRenew()) {
                renewMembership(membership.getId());
            }
        }
    }
    
    public void renewMembership(String membershipId) {
        UserMembership membership = membershipRepository.findById(membershipId)
            .orElseThrow(() -> new RuntimeException("Membership not found"));
        
        tierManager.recalculateTierForUser(membership.getUserId());
        
        membership.renew();
        membershipRepository.update(membership);
        
        PaymentResult result = paymentService.processPayment(
            membership.getUserId(),
            membership.getPlan().getPrice(),
            "Membership renewal for " + membership.getPlan().getName()
        );
        
        if (!result.isSuccess()) {
            handlePaymentFailure(membershipId);
            throw new RuntimeException("Payment failed: " + result.getErrorMessage());
        }
    }
    
    public void handlePaymentFailure(String membershipId) {
        UserMembership membership = membershipRepository.findById(membershipId)
            .orElseThrow(() -> new RuntimeException("Membership not found"));
        
        membership.setStatus(MembershipStatus.PAST_DUE);
        membershipRepository.update(membership);
    }
}
