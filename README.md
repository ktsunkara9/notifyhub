# NotifyHub

NotifyHub is a **production-grade, event-driven notification platform** built using **Quarkus and AWS serverless services**.  
It supports **real-time and bulk notifications** with **prioritization, rate limiting, user preferences**, and **multi-channel delivery** (Email, SMS, In-App, IVRS).

This project is designed to demonstrate **scalable system design**, **clean service boundaries**, and **cloud-native architecture** suitable for real-world production systems.

---

## 🛠️ Tech Stack

- **Framework**: Quarkus (supersonic, subatomic Java)
- **Language**: Java 17
- **Build Tool**: Maven
- **Infrastructure**: Terraform (Infrastructure as Code)
- **Cloud**: AWS Serverless (Lambda, API Gateway, DynamoDB, SQS, SNS)
- **Deployment**: Native compilation with GraalVM

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8.2 or higher (or use Maven Wrapper)
- AWS CLI (for authentication and optional debugging)
- Terraform 1.0+ (for infrastructure deployment)
- AWS Account with configured credentials

### Maven Wrapper Setup

This project uses Maven Wrapper to ensure consistent Maven version (3.9.6) across all environments.

**If Maven wrapper JAR is missing, download it:**
```bash
# Windows
curl -o .mvn\wrapper\maven-wrapper.jar https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar

# Unix/Linux/Mac
curl -o .mvn/wrapper/maven-wrapper.jar https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
```

**Or generate Maven Wrapper files from scratch:**
```bash
mvn -N wrapper:wrapper -Dmaven=3.9.6
```

This creates:
- `mvnw.cmd` (Windows)
- `mvnw` (Unix/Linux)
- `.mvn/wrapper/` directory

**After setup, use `mvnw` instead of `mvn`:**
```bash
# Windows
mvnw.cmd quarkus:dev

# Unix/Linux/Mac
./mvnw quarkus:dev
```

### Running the Application

#### Local Development

Run in development mode with hot reload:

```bash
mvnw quarkus:dev
```

Test the endpoints:

```bash
# Send notification
curl -X POST http://localhost:8080/api/v1/notifications \
  -H "Content-Type: application/json" \
  -d '{"userId":"123","message":"Hello"}'

# Health check
curl http://localhost:8080/health
```

**Dev Profile:**
- Uses `NotificationResource` (REST endpoints)
- Excludes `ApiHandler` (Lambda handler)
- Runs on `http://localhost:8080`

---

#### AWS Lambda Deployment

**Step 1: Build for Lambda**

```bash
# Build with prod profile
mvnw clean package -Dquarkus.profile=prod
```

This creates `target/function.zip` with:
- `ApiHandler` included (Lambda routing)
- `NotificationResource` excluded (not needed in Lambda)

**Step 2: Deploy Infrastructure**

```bash
cd terraform

# Initialize Terraform (first time only)
terraform init

# Review changes
terraform plan

# Deploy to AWS
terraform apply
```

**Step 3: Test on AWS**

```bash
# Get API endpoint from Terraform output
terraform output api_endpoint
```

**Test Health Endpoint:**
```bash
curl https://[api-id].execute-api.us-east-1.amazonaws.com/dev/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "service": "notifyhub"
}
```

**Test Notification Endpoint:**
```bash
curl -X POST https://[api-id].execute-api.us-east-1.amazonaws.com/dev/api/v1/notifications \
  -H "Content-Type: application/json" \
  -d '{"userId":"user123","message":"Hello from AWS"}'
```

**Expected Response:**
```json
{
  "notificationId": "abc-123",
  "status": "PENDING",
  "message": "Notification queued successfully"
}
```

**Step 4: Destroy Infrastructure**

```bash
cd terraform
terraform destroy
```

---

## 📚 Documentation

- **[NotifyHubApproach.md](NotifyHubApproach.md)** - Architecture decisions, framework comparison, and design patterns
- **[TroubleShooting.md](TroubleShooting.md)** - Common issues and solutions

---

## ✅ Implementation Progress

### Phase 1: Core Infrastructure ✅
- [x] Quarkus project setup with Maven
- [x] DTOs (NotificationRequest, NotificationResponse, HealthResponse)
- [x] Exception handling (GlobalExceptionMapper, ErrorResponse)
- [x] REST endpoints (NotificationResource, HealthResource)
- [x] Service layer (NotificationService)
- [x] In-memory queue implementation
- [x] Profile-based compilation (dev/prod)

### Phase 2: AWS Lambda Integration ✅
- [x] Custom Lambda handler (ApiHandler)
- [x] Request/response mapping templates
- [x] Build profiles for local vs Lambda
- [x] Lambda deployment package (function.zip)

### Phase 3: Terraform Infrastructure ✅
- [x] SQS module (queue + DLQ)
- [x] Lambda module (function + IAM roles)
- [x] API Gateway module (REST API + endpoints)
- [x] Root Terraform configuration
- [x] AWS integration (non-proxy) setup

### Phase 4: SQS Integration ✅
- [x] SQS client configuration
- [x] SQSQueueService implementation
- [x] Lambda SQS event source mapping
- [x] Message processing from SQS
- [x] Messages successfully queued and visible in SQS

### Phase 5: Notification Processing 📋
- [ ] Priority-based queue routing
- [ ] Notification validation
- [ ] Rate limiting implementation
- [ ] User preference filtering
- [ ] Retry mechanism with exponential backoff

### Phase 6: Multi-Channel Delivery 📋
- [ ] Email channel integration
- [ ] SMS channel integration
- [ ] In-App notification service
- [ ] IVRS integration
- [ ] Channel-specific error handling

### Phase 7: DynamoDB Integration 📋
- [ ] DynamoDB tables (users, preferences, notifications)
- [ ] User preference management
- [ ] Notification history tracking
- [ ] Rate limit counters

### Phase 8: Bulk Notifications 📋
- [ ] Bulk notification API endpoint
- [ ] Location-based filtering
- [ ] Segment-based filtering
- [ ] Batch processing optimization
- [ ] Progress tracking

### Phase 9: Monitoring & Observability 📋
- [ ] CloudWatch metrics
- [ ] Custom application metrics
- [ ] Distributed tracing (X-Ray)
- [ ] Alarms and notifications
- [ ] Dashboard creation

### Phase 10: Testing & Documentation 📋
- [ ] Unit tests
- [ ] Integration tests
- [ ] Load testing
- [ ] API documentation (OpenAPI/Swagger)
- [ ] Deployment guide

**Legend:**
- ✅ Completed
- 🚧 In Progress
- 📋 Planned

---

## 🏗️ Project Structure

```
notifyhub/
├── src/main/java/inc/skt/notifyhub/
│   ├── dto/                    # Data Transfer Objects
│   │   ├── NotificationRequest.java
│   │   ├── NotificationResponse.java
│   │   └── HealthResponse.java
│   ├── exception/              # Exception handling
│   │   ├── ErrorResponse.java
│   │   └── GlobalExceptionMapper.java
│   ├── infrastructure/         # Infrastructure layer
│   │   └── queue/             # Queue implementations
│   ├── lambda/                # AWS Lambda handlers
│   │   └── ApiHandler.java    # Custom routing handler (prod)
│   ├── resource/              # REST endpoints
│   │   ├── NotificationResource.java  # (dev only)
│   │   └── HealthResource.java        # (dev only)
│   └── service/               # Business logic
│       └── NotificationService.java
├── terraform/                 # Infrastructure as Code
│   ├── modules/
│   │   ├── sqs/              # SQS queue + DLQ
│   │   ├── lambda/           # Lambda function + IAM
│   │   └── api-gateway/      # API Gateway + endpoints
│   ├── main.tf               # Root configuration
│   ├── variables.tf          # Input variables
│   ├── outputs.tf            # Output values
│   └── terraform.tfvars      # Variable values
└── pom.xml                   # Maven configuration
```

---

#### Build Profiles

| Profile | Command | Active Classes | Use Case |
|---------|---------|----------------|----------|
| **dev** | `mvnw quarkus:dev` | NotificationResource, HealthResource | Local development with REST endpoints |
| **prod** | `mvnw package -Dquarkus.profile=prod` | ApiHandler | AWS Lambda deployment with custom routing |

**Key Differences:**
- **Dev Profile**: Uses Quarkus REST endpoints for local testing
- **Prod Profile**: Uses custom ApiHandler for AWS Lambda with non-proxy integration
- Profile-based compilation excludes unused classes from final package

---

#### Quick Reference

```bash
# Local development
mvnw quarkus:dev

# Build for AWS
mvnw clean package -Dquarkus.profile=prod

# Deploy to AWS
cd terraform && terraform apply

# Destroy AWS resources
cd terraform && terraform destroy
```

---

## 🚀 Key Features

- **Event-driven architecture** using SQS and SNS
- **Serverless-first design** (API Gateway, Lambda, DynamoDB)
- **Real-time & bulk notifications**
- **Priority-based processing**
  - High priority (OTP / critical alerts)
  - Low priority (promotional messages)
- **Rate limiting**
  - Per user / per client
  - Priority-aware throttling
- **User preference management**
  - Channel-level preferences
- **Multi-channel delivery**
  - Email
  - SMS
  - In-App
  - IVRS
- **Highly scalable & fault-tolerant**
- **AWS Free Tier friendly**

---

## 🧠 System Design Overview

NotifyHub follows a **hub-and-spoke, event-driven architecture**.

### High-level flow

1. **Notification Service API**
   - Entry point for single notifications
2. **Bulk Notification Service API**
   - Admin-triggered bulk notifications (location/segment-based)
3. **Async processing pipeline**
   - Validation & prioritization
   - Rate limiting
   - User preference filtering
4. **Notification Dispatcher**
   - Fan-out to channel-specific queues
5. **Channel delivery services**
   - Independent delivery per channel

All notifications (single or bulk) go through the **same ingestion and processing pipeline**, ensuring consistency and correctness.

---
## 🏗️ Initial Architecture Diagram
![NotifyHub Architecture](docs/notifyhub_architecture.png)


## 🏗️ Final Architecture (AWS Serverless)
![NotifyHub Serverless Architecture](docs/notifyhub_serverless_architecture.png)

### Core AWS Services Used

- **Amazon API Gateway (REST API)** – HTTP endpoints with AWS integration (non-proxy)
- **AWS Lambda** – Serverless compute with custom routing handler
- **Amazon SQS** – Message queue with Dead Letter Queue (DLQ)
- **CloudWatch Logs** – Lambda execution logs and API Gateway access logs
- **IAM Roles** – Lambda execution permissions for SQS access

---

## 🔁 End-to-End Flow

### Single Notification Flow

