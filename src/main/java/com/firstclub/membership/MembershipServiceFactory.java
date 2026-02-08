package com.firstclub.membership;

import com.firstclub.membership.repository.*;
import com.firstclub.membership.service.*;
import com.firstclub.membership.manager.*;
import com.firstclub.membership.calculator.*;
import com.firstclub.membership.observer.*;
import com.firstclub.membership.util.*;

public class MembershipServiceFactory {
    
    public static MembershipService createMembershipService() {
        IPlanRepository planRepo = new JdbcPlanRepository();
        ITierRepository tierRepo = new JdbcTierRepository();
        IMembershipRepository membershipRepo = new JdbcMembershipRepository();
        
        IUserService userService = new MockUserService();
        IPaymentService paymentService = new MockPaymentService();
        
        IDistributedLock distributedLock = new InMemoryDistributedLock();
        
        TierChangeNotifier tierChangeNotifier = new TierChangeNotifier();
        tierChangeNotifier.addObserver(new NotificationObserver());
        tierChangeNotifier.addObserver(new AnalyticsObserver());
        tierChangeNotifier.addObserver(new AuditObserver());
        
        CriteriaEvaluator criteriaEvaluator = new CriteriaEvaluator(userService);
        
        TierManager tierManager = new TierManager(
            tierRepo, membershipRepo, criteriaEvaluator, 
            distributedLock, tierChangeNotifier
        );
        RenewalManager renewalManager = new RenewalManager(
            membershipRepo, tierManager, paymentService
        );
        
        return new MembershipService(
            planRepo, tierRepo, membershipRepo, tierManager, 
            renewalManager, userService, paymentService, distributedLock
        );
    }
}
