package com.firstclub.membership.repository;

import com.firstclub.membership.domain.MembershipTier;
import java.util.List;
import java.util.Optional;

public interface ITierRepository {
    Optional<MembershipTier> findById(String id);
    List<MembershipTier> findAllActive();
    Optional<MembershipTier> findByLevel(Integer level);
    List<MembershipTier> findAllOrderedByLevel();  // Sorted by level DESC
    MembershipTier save(MembershipTier tier);
}
