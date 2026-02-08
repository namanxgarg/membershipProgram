package com.firstclub.membership.repository;

import com.firstclub.membership.domain.UserMembership;
import com.firstclub.membership.domain.MembershipPlan;
import com.firstclub.membership.domain.MembershipTier;
import com.firstclub.membership.domain.MembershipStatus;
import com.firstclub.membership.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMembershipRepository implements IMembershipRepository {
    private DatabaseConnection db;

    public JdbcMembershipRepository() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public Optional<UserMembership> findById(String id) {
        String sql = "SELECT * FROM user_memberships WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding membership: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserMembership> findByUserId(String userId) {
        String sql = "SELECT * FROM user_memberships WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding membership by user: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<UserMembership> findExpiringMemberships(LocalDateTime date) {
        List<UserMembership> memberships = new ArrayList<>();
        String sql = "SELECT * FROM user_memberships WHERE next_billing_date <= ? AND status = 'ACTIVE'";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                memberships.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding expiring memberships: " + e.getMessage());
        }
        return memberships;
    }

    @Override
    public UserMembership save(UserMembership membership) {
        String sql = "INSERT INTO user_memberships (id, user_id, plan_id, tier_id, status, " +
                     "start_date, end_date, next_billing_date, auto_renew) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, membership.getId());
            stmt.setString(2, membership.getUserId());
            stmt.setString(3, membership.getPlan().getId());
            stmt.setString(4, membership.getTier().getId());
            stmt.setString(5, membership.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(membership.getStartDate()));
            stmt.setTimestamp(7, Timestamp.valueOf(membership.getEndDate()));
            stmt.setTimestamp(8, Timestamp.valueOf(membership.getNextBillingDate()));
            stmt.setBoolean(9, membership.getAutoRenew());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving membership: " + e.getMessage());
        }
        return membership;
    }

    @Override
    public UserMembership update(UserMembership membership) {
        String sql = "UPDATE user_memberships SET plan_id=?, tier_id=?, status=?, " +
                     "start_date=?, end_date=?, next_billing_date=?, auto_renew=?, updated_at=CURRENT_TIMESTAMP " +
                     "WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, membership.getPlan().getId());
            stmt.setString(2, membership.getTier().getId());
            stmt.setString(3, membership.getStatus().name());
            stmt.setTimestamp(4, Timestamp.valueOf(membership.getStartDate()));
            stmt.setTimestamp(5, Timestamp.valueOf(membership.getEndDate()));
            stmt.setTimestamp(6, Timestamp.valueOf(membership.getNextBillingDate()));
            stmt.setBoolean(7, membership.getAutoRenew());
            stmt.setString(8, membership.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating membership: " + e.getMessage());
        }
        return membership;
    }

    private UserMembership mapRow(ResultSet rs) throws SQLException {
        // Load plan and tier
        IPlanRepository planRepo = new JdbcPlanRepository();
        ITierRepository tierRepo = new JdbcTierRepository();
        
        MembershipPlan plan = planRepo.findById(rs.getString("plan_id"))
            .orElseThrow(() -> new RuntimeException("Plan not found"));
        MembershipTier tier = tierRepo.findById(rs.getString("tier_id"))
            .orElseThrow(() -> new RuntimeException("Tier not found"));
        
        UserMembership membership = new UserMembership(
            rs.getString("id"),
            rs.getString("user_id"),
            plan,
            tier
        );
        
        // Set status and dates
        membership.setStatus(MembershipStatus.valueOf(rs.getString("status")));
        membership.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
        membership.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
        membership.setNextBillingDate(rs.getTimestamp("next_billing_date").toLocalDateTime());
        membership.setAutoRenew(rs.getBoolean("auto_renew"));
        
        return membership;
    }
}
