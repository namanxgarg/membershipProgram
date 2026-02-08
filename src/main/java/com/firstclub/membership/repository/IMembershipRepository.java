package com.firstclub.membership.repository;

import com.firstclub.membership.domain.UserMembership;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IMembershipRepository {
    Optional<UserMembership> findById(String id);
    Optional<UserMembership> findByUserId(String userId);
    List<UserMembership> findExpiringMemberships(LocalDateTime date);
    UserMembership save(UserMembership membership);
    UserMembership update(UserMembership membership);
}
