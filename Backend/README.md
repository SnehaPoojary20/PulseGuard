## PulseGuard Backend

This directory contains the **backend services** for PulseGuard — a distributed monitoring, alerting, and rate-limiting system designed for high-throughput, real-time environments.

The backend is built as a **set of independently deployable services**, each with a clear responsibility, enabling scalability, fault isolation, and low-latency event processing.

---

## Backend Architecture Overview

The PulseGuard backend is composed of three primary services:

Metric Producers
↓
FastAPI Ingestion API
↓
Asynchronous Worker Queue
↓
Redis (State + Sliding Window Logic)
↓
Node.js WebSocket Gateway
↓
Real-time Clients / Dashboards

yaml
Copy code

Each service is stateless and horizontally scalable, with Redis acting as the centralized coordination and state layer.

---

## Services

### 1. Ingestion API (FastAPI)

**Location:** `backend/ingestion-api/`

The ingestion service exposes versioned HTTP APIs to receive metrics, logs, and monitoring events from external services.

**Responsibilities:**
- Accept incoming monitoring data
- Perform schema validation
- Enqueue events for asynchronous processing
- Remain lightweight and non-blocking under load

**Key Characteristics:**
- Async-first FastAPI implementation
- No alerting logic in request path
- Optimized for high request throughput

---

### 2. Alert Processing Worker

**Location:** `backend/worker/`

The worker service performs all **CPU- and I/O-intensive tasks** asynchronously to avoid blocking ingestion traffic.

**Responsibilities:**
- Aggregate metrics over time windows
- Evaluate alerting rules
- Apply sliding window rate limiting to alerts
- Prevent alert storms during traffic spikes
- Publish alert events for downstream delivery

**Key Characteristics:**
- Redis-backed state management
- Sliding window rate limiter for alert throttling
- Designed for horizontal scaling

---

### 3. Alert Gateway (WebSocket Server)

**Location:** `backend/alert-gateway/`

The alert gateway is responsible for **real-time delivery** of alerts to connected clients.

**Responsibilities:**
- Subscribe to alert events via Redis Pub/Sub
- Maintain WebSocket connections
- Broadcast alerts with sub-second latency

**Key Characteristics:**
- Event-driven Node.js architecture
- Low-latency push-based communication
- Decoupled from alert evaluation logic

---

## Redis Usage

Redis serves as the **central coordination layer** across backend services.

It is used for:
- Sliding window counters for rate limiting
- Alert deduplication and state tracking
- Inter-service messaging (Pub/Sub or Streams)
- TTL-based cleanup to prevent stale state

All rate-limit and alerting decisions are designed to be **atomic and race-condition free**.

---

## Technology Stack

- **FastAPI** – Ingestion service
- **Node.js** – WebSocket alert gateway
- **Redis** – Centralized state, rate limiting, messaging
- **Async Workers** – Background processing
- **Docker / Docker Compose** – Local orchestration

---

## Running Backend Services Locally

From the project root:

```bash
docker-compose up --build
This starts:

Redis

Ingestion API

Worker service

Alert gateway

Each service can also be started independently for development.

Design Principles
Stateless services for horizontal scalability

Clear separation of concerns

Asynchronous processing for performance

Centralized state with atomic operations

Failure-aware design to maintain system stability

Observability & Reliability
The backend is designed to support:

Request and alert throughput monitoring

Rate-limit enforcement metrics

Redis latency tracking

Graceful degradation during traffic spikes

These metrics can be integrated with external monitoring systems.

Author
Sneha Poojary
