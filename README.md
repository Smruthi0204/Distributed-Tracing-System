# Distributed Tracing System

In a microservice architecture, a single user request can touch multiple services before completing — and when something goes wrong, finding exactly where 
and why is painful without proper visibility. This project solves that problem by tracking the full execution journey of any request across services, 
giving engineers the tools to query, filter, and analyze traces. An AI-powered analysis engine examines the trace data and delivers a precise diagnosis of 
what went wrong and where. Three simulated microservices demonstrate the end-to-end flow.

---

## The problem addressed

In a distributed system, a single user request touches multiple services before completing. 
When something breaks or slows down, there is no single log file to look at — each service 
only knows its own piece of the story. Engineers end up manually correlating logs across 
services, which is time-consuming and error-prone.

This system solves that by giving every request a shared trace ID that follows it across 
all services. Each service reports what it did as a span, the collector stitches them 
together, and engineers can instantly see the full picture — what happened, where it 
failed, and which service is responsible. An AI-powered analysis endpoint takes it further — 
pass it a trace ID and get a plain-English explanation of what went wrong, saving engineers 
the time of manually reading through spans to find the root cause.

---

## Features

- Collect and store span data from distributed microservices
- Query full request journeys, failures, and slowest operations across services
- AI-powered root cause analysis on any trace via Groq LLM
- Redis caching for repeated trace lookups
- Fully containerized — entire system starts with a single command!

---

## System Architecture

![Architecture Diagram](./Architecture-Diagram.jpg).

---

## Event Flow

1. A microservice (API Gateway, Order Service, or Payment Service) handles a request 
   and sends a span to the Trace Collector via POST /api/spans. All spans belonging 
   to the same request share a common trace ID.

2. The Trace Collector receives the span and saves it to PostgreSQL.

3. When an engineer queries a trace by ID, the collector first checks Redis. If the 
   trace is cached, it returns immediately without hitting the database. If not, it 
   fetches from PostgreSQL and caches the result in Redis for future lookups.

4. The engineer can query:
   - All spans for a specific trace ID to see the full request journey
   - All failed traces to identify what is currently broken
   - The slowest traces to find performance bottlenecks

5. For any trace, the engineer can call the analyze endpoint. The collector fetches 
   the spans, sends them to an LLM, and returns a plain-English explanation of what 
   went wrong and which service caused it.

---

   ## Live Demo Walkthrough

[View Demo →](./demo.pdf)

---

   ## Tech Stack

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

---

## API Reference

| Method | Endpoint | Description | Response |
|---|---|---|---|
| POST | `/api/spans` | Receive a span from a microservice and store it | `202 Accepted` |
| GET | `/api/traces/{traceId}` | Fetch all spans for a trace. Checks Redis first, falls back to PostgreSQL on cache miss | `200 OK` — list of spans |
| GET | `/api/traces/failed` | Fetch all spans with status ERROR | `200 OK` — list of spans |
| GET | `/api/traces/slowest` | Fetch all spans ordered by duration, slowest first | `200 OK` — list of spans |
| GET | `/api/traces/{traceId}/analyze` | Send trace spans to LLM and return plain-English root cause analysis | `200 OK` — analysis text |

### Sample Request — POST /api/spans
```json
{
    "traceId": "trace-2001",
    "spanId": "span-001",
    "serviceName": "order-service",
    "operationName": "POST /orders",
    "startTime": 1000,
    "endTime": 1200,
    "status": "OK"
}
```

---

## Design Decisions

**Why PostgreSQL over MongoDB?**
Span queries are relational by nature — fetch all spans where traceId = X, filter by status, 
order by duration. These are structured queries on fixed-schema data. PostgreSQL handles this 
with indexed lookups and native ORDER BY on computed columns. MongoDB is optimized for 
flexible, document-shaped data where the schema varies — spans always have the same fields, 
so the flexibility MongoDB offers goes unused while its query performance on relational 
patterns is weaker.

**Why Redis for caching?**
When something breaks in production, the same trace gets queried repeatedly — by the engineer 
debugging, teammates reviewing, and monitoring dashboards refreshing. Redis stores the result 
of a trace lookup in memory after the first DB hit. Every subsequent request for the same 
trace ID is served in microseconds without touching PostgreSQL. This matters especially under 
incident load, when query volume spikes precisely when the system is already under stress.

**Why cache at the trace level and not the span level?**
Engineers query by trace ID, not by individual span ID. Caching the full list of spans per 
trace ID matches the actual access pattern — one key, one result set, served instantly.

**Why 202 Accepted over 200 OK?**
200 means the request was fully processed. A collector's job is to receive fast and not block 
the calling service. 202 accurately signals that the span was received and accepted for 
processing, not that it has already been persisted — the semantically correct status for a 
fire-and-forget ingestion endpoint.
