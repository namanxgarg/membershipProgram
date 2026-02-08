package com.firstclub.membership.repository;

import com.firstclub.membership.domain.MembershipPlan;
import com.firstclub.membership.db.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPlanRepository implements IPlanRepository {
    private DatabaseConnection db;

    public JdbcPlanRepository() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public Optional<MembershipPlan> findById(String id) {
        String sql = "SELECT * FROM membership_plans WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding plan: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<MembershipPlan> findAllActive() {
        List<MembershipPlan> plans = new ArrayList<>();
        String sql = "SELECT * FROM membership_plans WHERE is_active = true ORDER BY duration_months";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                plans.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding active plans: " + e.getMessage());
        }
        return plans;
    }

    @Override
    public MembershipPlan save(MembershipPlan plan) {
        String sql = "MERGE INTO membership_plans (id, name, price, duration_months, is_active, updated_at) " +
                     "KEY(id) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plan.getId());
            stmt.setString(2, plan.getName());
            stmt.setBigDecimal(3, plan.getPrice());
            stmt.setInt(4, plan.getDurationMonths());
            stmt.setBoolean(5, plan.getIsActive());
            stmt.executeUpdate();
        } catch (SQLException e) {
            try {
                String insertSql = "INSERT INTO membership_plans (id, name, price, duration_months, is_active) " +
                                 "VALUES (?, ?, ?, ?, ?) " +
                                 "ON DUPLICATE KEY UPDATE name=?, price=?, duration_months=?, is_active=?, updated_at=CURRENT_TIMESTAMP";
                try (Connection conn = db.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setString(1, plan.getId());
                    stmt.setString(2, plan.getName());
                    stmt.setBigDecimal(3, plan.getPrice());
                    stmt.setInt(4, plan.getDurationMonths());
                    stmt.setBoolean(5, plan.getIsActive());
                    stmt.setString(6, plan.getName());
                    stmt.setBigDecimal(7, plan.getPrice());
                    stmt.setInt(8, plan.getDurationMonths());
                    stmt.setBoolean(9, plan.getIsActive());
                    stmt.executeUpdate();
                }
            } catch (SQLException e2) {
                System.err.println("Error saving plan: " + e2.getMessage());
            }
        }
        return plan;
    }

    private MembershipPlan mapRow(ResultSet rs) throws SQLException {
        return new MembershipPlan(
            rs.getString("id"),
            rs.getString("name"),
            rs.getBigDecimal("price"),
            rs.getInt("duration_months")
        );
    }
}
