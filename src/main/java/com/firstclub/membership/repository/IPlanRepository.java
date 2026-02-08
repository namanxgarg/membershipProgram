package com.firstclub.membership.repository;

import com.firstclub.membership.domain.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IPlanRepository {
    Optional<MembershipPlan> findById(String id);
    List<MembershipPlan> findAllActive();
    MembershipPlan save(MembershipPlan plan);
}
