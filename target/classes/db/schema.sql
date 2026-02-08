-- Membership Plans Table
CREATE TABLE IF NOT EXISTS membership_plans (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    duration_months INT NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Membership Tiers Table
CREATE TABLE IF NOT EXISTS membership_tiers (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    level INT NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tier Criteria Table
CREATE TABLE IF NOT EXISTS tier_criteria (
    id VARCHAR(50) PRIMARY KEY,
    tier_id VARCHAR(50) NOT NULL,
    criteria_type VARCHAR(50) NOT NULL,
    threshold DECIMAL(10,2),
    time_window_days INT,
    cohort_id VARCHAR(50),
    logical_operator VARCHAR(10) DEFAULT 'OR',
    FOREIGN KEY (tier_id) REFERENCES membership_tiers(id)
);

-- Tier Benefits Table
CREATE TABLE IF NOT EXISTS tier_benefits (
    id VARCHAR(50) PRIMARY KEY,
    tier_id VARCHAR(50) NOT NULL,
    benefit_type VARCHAR(50) NOT NULL,
    value DECIMAL(5,2),
    category_id VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    FOREIGN KEY (tier_id) REFERENCES membership_tiers(id)
);

-- User Memberships Table
CREATE TABLE IF NOT EXISTS user_memberships (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    plan_id VARCHAR(50) NOT NULL,
    tier_id VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    next_billing_date TIMESTAMP NOT NULL,
    auto_renew BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plan_id) REFERENCES membership_plans(id),
    FOREIGN KEY (tier_id) REFERENCES membership_tiers(id)
);

-- User Stats Table
CREATE TABLE IF NOT EXISTS user_stats (
    user_id VARCHAR(50) PRIMARY KEY,
    total_orders INT DEFAULT 0,
    total_order_value DECIMAL(10,2) DEFAULT 0,
    last_order_date TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
