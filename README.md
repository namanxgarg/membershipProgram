# FirstClub Membership Service

A membership management service with tier-based benefits, automatic tier upgrades, and subscription management.

## Features

- **Membership Plans**: Monthly, Quarterly, and Yearly subscription options
- **Tier System**: Silver, Gold, and Platinum tiers with automatic upgrades
- **Tier Benefits**: Discounts, free delivery, exclusive deals, and more
- **Order Processing**: Automatic tier recalculation based on order history
- **RESTful API**: Complete API for membership management

## Prerequisites

- Docker Desktop installed and running
- Docker Compose (usually included with Docker Desktop)
- `curl` and `jq` (optional, for testing)

## Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd membershipProgram-main
   ```

2. **Start the application**
   ```bash
   docker-compose up --build -d
   ```

3. **Wait for services to start** (about 10-15 seconds)
   ```bash
   docker-compose logs -f membership-service
   ```
   Press `Ctrl+C` to exit logs.

4. **Verify the service is running**
   ```bash
   curl http://localhost:8080/api/membership/plans
   ```

## API Endpoints

### Base URL
```
http://localhost:8080/api
```

### GET Endpoints

#### 1. Get Available Membership Plans
```bash
curl http://localhost:8080/api/membership/plans
```

**Response:**
```json
[
  {
    "id": "plan_monthly",
    "name": "Monthly",
    "price": 9.99,
    "durationMonths": 1,
    "isActive": true
  },
  {
    "id": "plan_quarterly",
    "name": "Quarterly",
    "price": 24.99,
    "durationMonths": 3,
    "isActive": true
  },
  {
    "id": "plan_yearly",
    "name": "Yearly",
    "price": 89.99,
    "durationMonths": 12,
    "isActive": true
  }
]
```

#### 2. Get Available Membership Tiers
```bash
curl http://localhost:8080/api/membership/tiers
```

**Response:** Returns all tiers (Silver, Gold, Platinum) with their criteria and benefits.

#### 3. Get Current Membership
```bash
curl "http://localhost:8080/api/membership/current?userId=user123"
```

**Response:** Returns the current membership status for the user.

#### 4. Get Membership Benefits
```bash
curl "http://localhost:8080/api/internal/membership/benefits?userId=user123"
```

**Response:** Returns tier benefits including discounts and free delivery status.

#### 5. Get Tier History (Admin)
```bash
curl "http://localhost:8080/api/admin/membership/tier-history?userId=user123"
```

**Response:** Returns 501 (Not implemented yet)

### POST Endpoints

#### 6. Subscribe to Membership
```bash
curl -X POST http://localhost:8080/api/membership/subscribe \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "planId": "plan_monthly",
    "tierId": "tier_silver"
  }'
```

**Response:** Returns the created membership object with ID, dates, and status.

#### 7. Cancel Membership
```bash
curl -X POST http://localhost:8080/api/membership/cancel \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123"
  }'
```

**Response:**
```json
{
  "success": "Membership cancelled"
}
```

#### 8. Process Order Completion
```bash
curl -X POST http://localhost:8080/api/internal/membership/order-completed \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "orderValue": 600.00
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Order processed, tier recalculated"
}
```

**Note:** This endpoint automatically recalculates and upgrades the user's tier based on order history.

#### 9. Admin Upgrade Tier (Not Implemented)
```bash
curl -X POST http://localhost:8080/api/admin/membership/upgrade-tier \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "newTierId": "tier_gold",
    "reason": "MANUAL_UPGRADE"
  }'
```

**Response:** Returns 501 (Not implemented yet)

## Complete Test Workflow

Here's a complete workflow to test all functionality:

```bash
# 1. Check available plans
echo "=== 1. Available Plans ==="
curl -s http://localhost:8080/api/membership/plans | jq '.'

# 2. Check available tiers
echo -e "\n=== 2. Available Tiers ==="
curl -s http://localhost:8080/api/membership/tiers | jq 'length' && echo "tiers found"

# 3. Subscribe a user
echo -e "\n=== 3. Subscribe User ==="
curl -s -X POST http://localhost:8080/api/membership/subscribe \
  -H "Content-Type: application/json" \
  -d '{"userId":"user123","planId":"plan_monthly","tierId":"tier_silver"}' | jq '.id'

# 4. Check current membership
echo -e "\n=== 4. Current Membership ==="
curl -s "http://localhost:8080/api/membership/current?userId=user123" | jq '.present'

# 5. Get membership benefits
echo -e "\n=== 5. Membership Benefits ==="
curl -s "http://localhost:8080/api/internal/membership/benefits?userId=user123" | jq '.tierName'

# 6. Process an order (triggers tier upgrade)
echo -e "\n=== 6. Process Order (Value: 600) ==="
curl -s -X POST http://localhost:8080/api/internal/membership/order-completed \
  -H "Content-Type: application/json" \
  -d '{"userId":"user123","orderValue":600.00}' | jq '.success'

# 7. Check updated membership (should be Gold tier now)
echo -e "\n=== 7. Updated Membership (After Order) ==="
curl -s "http://localhost:8080/api/membership/current?userId=user123" | jq 'if .present then .get.tier.id else "No membership" end'

# 8. Check updated benefits
echo -e "\n=== 8. Updated Benefits (Gold Tier) ==="
curl -s "http://localhost:8080/api/internal/membership/benefits?userId=user123" | jq '.tierName'

# 9. Cancel membership
echo -e "\n=== 9. Cancel Membership ==="
curl -s -X POST http://localhost:8080/api/membership/cancel \
  -H "Content-Type: application/json" \
  -d '{"userId":"user123"}' | jq '.success'

# 10. Verify cancellation
echo -e "\n=== 10. Verify Cancellation ==="
curl -s "http://localhost:8080/api/membership/current?userId=user123" | jq '.present'
```

## Tier System

### Silver Tier (Default)
- **Criteria**: None (default tier for all new members)
- **Benefits**:
  - Free Delivery
  - 5% Discount

### Gold Tier
- **Criteria** (OR logic):
  - 5+ orders in 30 days, OR
  - $500+ total order value in 30 days
- **Benefits**:
  - Free Delivery
  - 10% Discount
  - Exclusive Deals

### Platinum Tier
- **Criteria** (AND logic):
  - 15+ orders in 30 days, AND
  - $1500+ total order value in 30 days
- **Benefits**:
  - Free Delivery
  - 15% Discount (general)
  - 20% Discount (electronics category)
  - Exclusive Deals
  - Early Access
  - Priority Support

## Database

The application uses PostgreSQL and automatically initializes:
- Database schema (tables)
- Seed data (plans, tiers, criteria, benefits)

**Database Connection:**
- Host: `postgres` (within Docker network)
- Port: `5432`
- Database: `membershipdb`
- Username: `postgres`
- Password: `postgres`

## Docker Commands

### Start Services
```bash
docker-compose up -d
```

### Stop Services
```bash
docker-compose down
```

### Stop and Remove Volumes (Clean Start)
```bash
docker-compose down -v
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f membership-service
docker-compose logs -f postgres
```

### Rebuild and Restart
```bash
docker-compose down
docker-compose up --build -d
```

### Check Container Status
```bash
docker-compose ps
```

## Project Structure

```
membershipProgram-main/
├── src/
│   └── main/
│       ├── java/com/firstclub/membership/
│       │   ├── api/              # REST API endpoints
│       │   ├── calculator/       # Tier criteria evaluation
│       │   ├── db/               # Database connection
│       │   ├── domain/           # Domain models
│       │   ├── manager/          # Business logic managers
│       │   ├── observer/         # Observer pattern for tier changes
│       │   ├── repository/       # Data access layer
│       │   ├── service/          # Business services
│       │   └── util/             # Utility classes
│       └── resources/
│           ├── application.properties
│           └── db/
│               ├── schema.sql    # Database schema
│               └── data.sql      # Seed data
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Troubleshooting

### Service won't start
1. Check if Docker is running: `docker ps`
2. Check logs: `docker-compose logs membership-service`
3. Ensure port 8080 is not in use: `lsof -i :8080`

### Database connection errors
1. Wait for PostgreSQL to be healthy: `docker-compose ps`
2. Check PostgreSQL logs: `docker-compose logs postgres`
3. Restart services: `docker-compose restart`

### Empty API responses
1. Check if database initialized: `docker logs membership-service | grep "Database initialized"`
2. Verify tables exist: `docker exec membership-postgres psql -U postgres -d membershipdb -c "\dt"`
