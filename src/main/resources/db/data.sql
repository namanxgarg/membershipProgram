-- Insert Membership Plans
INSERT INTO membership_plans (id, name, price, duration_months, is_active) VALUES
('plan_monthly', 'Monthly', 9.99, 1, true),
('plan_quarterly', 'Quarterly', 24.99, 3, true),
('plan_yearly', 'Yearly', 89.99, 12, true)
ON CONFLICT (id) DO NOTHING;

-- Insert Membership Tiers
INSERT INTO membership_tiers (id, name, level, is_active) VALUES
('tier_silver', 'Silver', 1, true),
('tier_gold', 'Gold', 2, true),
('tier_platinum', 'Platinum', 3, true)
ON CONFLICT (id) DO NOTHING;

-- Insert Tier Criteria (Silver - no criteria, default tier)
-- No criteria for Silver - everyone starts here

-- Insert Tier Criteria (Gold)
INSERT INTO tier_criteria (id, tier_id, criteria_type, threshold, time_window_days, logical_operator) VALUES
('criteria_gold_1', 'tier_gold', 'ORDER_COUNT', 5, 30, 'OR'),
('criteria_gold_2', 'tier_gold', 'ORDER_VALUE', 500, 30, 'OR')
ON CONFLICT (id) DO NOTHING;

-- Insert Tier Criteria (Platinum)
INSERT INTO tier_criteria (id, tier_id, criteria_type, threshold, time_window_days, logical_operator) VALUES
('criteria_platinum_1', 'tier_platinum', 'ORDER_COUNT', 15, 30, 'AND'),
('criteria_platinum_2', 'tier_platinum', 'ORDER_VALUE', 1500, 30, 'AND')
ON CONFLICT (id) DO NOTHING;

-- Insert Tier Benefits (Silver)
INSERT INTO tier_benefits (id, tier_id, benefit_type, value, category_id, is_active) VALUES
('benefit_silver_1', 'tier_silver', 'FREE_DELIVERY', NULL, NULL, true),
('benefit_silver_2', 'tier_silver', 'DISCOUNT_PERCENTAGE', 5.00, NULL, true)
ON CONFLICT (id) DO NOTHING;

-- Insert Tier Benefits (Gold)
INSERT INTO tier_benefits (id, tier_id, benefit_type, value, category_id, is_active) VALUES
('benefit_gold_1', 'tier_gold', 'FREE_DELIVERY', NULL, NULL, true),
('benefit_gold_2', 'tier_gold', 'DISCOUNT_PERCENTAGE', 10.00, NULL, true),
('benefit_gold_3', 'tier_gold', 'EXCLUSIVE_DEALS', NULL, NULL, true)
ON CONFLICT (id) DO NOTHING;

-- Insert Tier Benefits (Platinum)
INSERT INTO tier_benefits (id, tier_id, benefit_type, value, category_id, is_active) VALUES
('benefit_platinum_1', 'tier_platinum', 'FREE_DELIVERY', NULL, NULL, true),
('benefit_platinum_2', 'tier_platinum', 'DISCOUNT_PERCENTAGE', 15.00, NULL, true),
('benefit_platinum_3', 'tier_platinum', 'DISCOUNT_PERCENTAGE', 20.00, 'electronics', true),
('benefit_platinum_4', 'tier_platinum', 'EXCLUSIVE_DEALS', NULL, NULL, true),
('benefit_platinum_5', 'tier_platinum', 'EARLY_ACCESS', NULL, NULL, true),
('benefit_platinum_6', 'tier_platinum', 'PRIORITY_SUPPORT', NULL, NULL, true)
ON CONFLICT (id) DO NOTHING;
