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
- GraalVM (optional, for native compilation)
- Docker Desktop (for native builds on Windows)

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
curl http://localhost:8080/q/health
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

# Test notification endpoint
curl -X POST https://[api-id].execute-api.us-east-1.amazonaws.com/dev/api/v1/notifications \
  -H "Content-Type: application/json" \
  -d '{"userId":"123","message":"Hello from AWS"}'

# Test health endpoint
curl https://[api-id].execute-api.us-east-1.amazonaws.com/dev/health
```

**Step 4: Destroy Infrastructure**

```bash
cd terraform
terraform destroy
```

---

#### Build Profiles

| Profile | Command | Active Class | Use Case |
|---------|---------|--------------|----------|
| **dev** | `mvnw quarkus:dev` | NotificationResource | Local development |
| **prod** | `mvnw package -Dquarkus.profile=prod` | ApiHandler | AWS Lambda deployment |

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

- **Amazon API Gateway** – Public APIs
- **AWS Lambda** – Stateless compute
- **Amazon SQS** – Async queues & back-pressure handling
- **Amazon SNS** – Fan-out to multiple channels
- **Amazon DynamoDB** – User data, preferences, rate-limit counters
- **Amazon S3 + CloudFront** – Static UI hosting (Bulk Notification UI)

---

## 🔁 End-to-End Flow

### Single Notification Flow

