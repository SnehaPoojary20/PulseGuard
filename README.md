## PulseGuard 
Distributed Sliding Window Rate Limiter

PulseGuard is a distributed, fault-aware rate limiting service
designed to protect APIs in high-throughput systems. It enforces
request limits atomically across multiple stateless application
servers using Redis and Lua scripting.

## Why PulseGuard?

In large-scale systems, requests from a single user can hit
multiple servers simultaneously. Traditional in-memory or
fixed-window rate limiters break down due to race conditions and
unfair request bursts at window boundaries.

## PulseGuard addresses these challenges by:

Centralizing shared state in Redis

Using atomic Lua scripts to prevent race conditions

Implementing a Sliding Window Counter for fair traffic shaping

## Core Concepts
Sliding Window Counter

Unlike fixed windows, sliding windows prevent traffic bursts
at time boundaries by blending request counts from adjacent windows.

effective_requests =
  current_window_count
+ previous_window_count × overlap_ratio


This ensures smoother enforcement and fair usage across time windows.

All rate-limit decisions are computed atomically inside Redis.

## Architecture

Client
  ↓
Stateless API Servers (N)
  ↓
Redis (Centralized State + Lua Scripts)


 Multiple application servers safely coordinate through Redis without
shared in-memory state.

## Key Features

Distributed rate limiting across multiple servers

Sliding window algorithm (not fixed window)

Atomic Redis Lua scripts (race-condition free)

Per-user / per-IP / per-endpoint rate limits

Dynamic rate limit configuration

Graceful degradation during Redis failures

Built-in observability and metrics

## Technology Stack

Java 17

Redis

Lua (atomic scripts)

Docker / Docker Compose

## How It Works (High Level)

API server extracts a rate-limit key (user / IP / endpoint)

Server invokes a Redis Lua script

The script:

Reads current and previous window counters

Computes the weighted request count

Atomically increments the current window

Returns ALLOW or DENY

The API server enforces the decision

Failure Handling

Redis timeouts trigger a configurable fallback strategy

TTL-based key expiration prevents memory leaks

Redis server time is used to avoid clock skew across nodes

Observability

PulseGuard exposes operational insights including:

Allowed vs rejected request counts

Redis latency metrics

Top offending users / IPs

These metrics can be exported to monitoring systems such as Prometheus.

Tradeoffs & Design Decisions
Decision	Reason
Redis + Lua	Guarantees atomicity across multiple operations
Sliding Window	Prevents burst traffic at window boundaries
Stateless servers	Enables horizontal scalability
Getting Started
git clone 
cd pulseguard
docker-compose up


The service will start with Redis and multiple stateless API servers.

## Future Improvements

Hybrid Token Bucket + Sliding Window model

Redis Cluster support

Adaptive rate limits based on historical traffic

Author

Sneha Poojary
Backend / Distributed Systems Enthusiast


