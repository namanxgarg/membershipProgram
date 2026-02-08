package com.firstclub.membership.repository;

import com.firstclub.membership.domain.MembershipTier;
import com.firstclub.membership.domain.TierCriteria;
import com.firstclub.membership.domain.TierBenefit;
import com.firstclub.membership.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTierRepository implements ITierRepository {
    private DatabaseConnection db;

    public JdbcTierRepository() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public Optional<MembershipTier> findById(String id) {
        String sql = "SELECT * FROM membership_tiers WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                MembershipTier tier = mapRow(rs);
                loadCriteriaAndBenefits(tier);
                return Optional.of(tier);
            }
        } catch (SQLException e) {
            System.err.println("Error finding tier: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<MembershipTier> findAllActive() {
        List<MembershipTier> tiers = new ArrayList<>();
        String sql = "SELECT * FROM membership_tiers WHERE is_active = true ORDER BY level";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MembershipTier tier = mapRow(rs);
                loadCriteriaAndBenefits(tier);
                tiers.add(tier);
            }
        } catch (SQLException e) {
            System.err.println("Error finding active tiers: " + e.getMessage());
        }
        return tiers;
    }

    @Override
    public Optional<MembershipTier> findByLevel(Integer level) {
        String sql = "SELECT * FROM membership_tiers WHERE level = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, level);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                MembershipTier tier = mapRow(rs);
                loadCriteriaAndBenefits(tier);
                return Optional.of(tier);
            }
        } catch (SQLException e) {
            System.err.println("Error finding tier by level: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<MembershipTier> findAllOrderedByLevel() {
        List<MembershipTier> tiers = new ArrayList<>();
        String sql = "SELECT * FROM membership_tiers WHERE is_active = true ORDER BY level DESC";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MembershipTier tier = mapRow(rs);
                loadCriteriaAndBenefits(tier);
                tiers.add(tier);
            }
        } catch (SQLException e) {
            System.err.println("Error finding tiers ordered by level: " + e.getMessage());
        }
        return tiers;
    }

    @Override
    public MembershipTier save(MembershipTier tier) {
        String sql = "MERGE INTO membership_tiers (id, name, level, is_active, updated_at) " +
                     "KEY(id) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tier.getId());
            stmt.setString(2, tier.getName());
            stmt.setInt(3, tier.getLevel());
            stmt.setBoolean(4, tier.getIsActive());
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Fallback
            try {
                String insertSql = "INSERT INTO membership_tiers (id, name, level, is_active) " +
                                 "VALUES (?, ?, ?, ?) " +
                                 "ON DUPLICATE KEY UPDATE name=?, level=?, is_active=?, updated_at=CURRENT_TIMESTAMP";
                try (Connection conn = db.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setString(1, tier.getId());
                    stmt.setString(2, tier.getName());
                    stmt.setInt(3, tier.getLevel());
                    stmt.setBoolean(4, tier.getIsActive());
                    stmt.setString(5, tier.getName());
                    stmt.setInt(6, tier.getLevel());
                    stmt.setBoolean(7, tier.getIsActive());
                    stmt.executeUpdate();
                }
            } catch (SQLException e2) {
                System.err.println("Error saving tier: " + e2.getMessage());
            }
        }
        return tier;
    }

    private MembershipTier mapRow(ResultSet rs) throws SQLException {
        return new MembershipTier(
            rs.getString("id"),
            rs.getString("name"),
            rs.getInt("level")
        );
    }

    private void loadCriteriaAndBenefits(MembershipTier tier) {
        loadCriteria(tier);
        loadBenefits(tier);
    }

    private void loadCriteria(MembershipTier tier) {
        String sql = "SELECT * FROM tier_criteria WHERE tier_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tier.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TierCriteria criteria = new TierCriteria(
                    rs.getString("id"),
                    rs.getString("tier_id"),
                    com.firstclub.membership.domain.CriteriaType.valueOf(rs.getString("criteria_type"))
                );
                if (rs.getBigDecimal("threshold") != null) {
                    criteria.withThreshold(rs.getBigDecimal("threshold"));
                }
                if (rs.getInt("time_window_days") > 0) {
                    criteria.withTimeWindow(rs.getInt("time_window_days"));
                }
                if (rs.getString("cohort_id") != null) {
                    criteria.withCohortId(rs.getString("cohort_id"));
                }
                if (rs.getString("logical_operator") != null) {
                    criteria.withOperator(com.firstclub.membership.domain.LogicalOperator.valueOf(
                        rs.getString("logical_operator")));
                }
                tier.addCriteria(criteria);
            }
        } catch (SQLException e) {
            System.err.println("Error loading criteria: " + e.getMessage());
        }
    }

    private void loadBenefits(MembershipTier tier) {
        String sql = "SELECT * FROM tier_benefits WHERE tier_id = ? AND is_active = true";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tier.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TierBenefit benefit = new TierBenefit(
                    rs.getString("id"),
                    rs.getString("tier_id"),
                    com.firstclub.membership.domain.BenefitType.valueOf(rs.getString("benefit_type"))
                );
                if (rs.getBigDecimal("value") != null) {
                    benefit.withValue(rs.getBigDecimal("value"));
                }
                if (rs.getString("category_id") != null) {
                    benefit.withCategoryId(rs.getString("category_id"));
                }
                tier.addBenefit(benefit);
            }
        } catch (SQLException e) {
            System.err.println("Error loading benefits: " + e.getMessage());
        }
    }
}
