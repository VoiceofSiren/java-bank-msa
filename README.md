# java-bank-msa

**Multi-module Spring Boot sample (Bank MSA)**

> **Summary**
>
> `java-bank-msa` is a multi-module Spring Boot sample project that demonstrates a banking-like microservice architecture. The repository is organized into separate modules for API, core business logic, domain models, event handling, and monitoring. This README highlights the repository layout, how CQRS and distributed locking are applied in the project, how to run and build the modules, and some operational/implementation notes.


---

## Project overview

`java-bank-msa` is a multi-module Java project (Spring Boot) that implements a simple banking microservices sample. The repository is organized into multiple modules to separate concerns such as API, core logic, domain models, events, and monitoring.

> Modules observed in the repository root:
> - `bank-api`
> - `bank-core`
> - `bank-domain`
> - `bank-event`
> - `bank-monitoring`
> - `gradle/` (wrapper)

(These module names come from the repository's top-level listing.)

---

## Goals & Intent

The project appears intended to demonstrate multi-module Spring Boot architecture for a banking-like domain, including:

- clear separation between API, domain and core business logic
- event handling / domain events
- monitoring support
- Gradle multi-module build

---

## Tech stack

- Java 17
- Spring Boot (multi-module apps usually use Spring Boot)
- Gradle wrapper (`gradlew` / `gradlew.bat` present)

(See the repository's `build.gradle` and module `build.gradle` files for exact versions and plugin configuration.)

---

## Project structure (high level)

```
java-bank-msa/
├─ bank-api/           # REST controllers, DTOs, API layer
├─ bank-core/          # core services, business logic
├─ bank-domain/        # domain model objects, entities
├─ bank-event/         # domain events, event publishers/listeners
├─ bank-monitoring/    # monitoring / observability related code
├─ gradle/             # gradle wrapper files
├─ build.gradle
├─ settings.gradle
├─ gradlew
└─ gradlew.bat
```
- Archiecture Diagram
![img.png](img.png)

> Note: the exact package names and class locations are found inside each module — open module folders to inspect controllers, services, repositories and event handlers.

---

## CQRS & event-driven flow (how it is applied)

This project applies CQRS-style separation between **write** and **read** responsibilities using an event-driven approach. The `bank-event` module contains event publishers and consumers that connect domain writes to read-model updates.

Typical flow on an *order* operation (conceptual):

1. **Write side (order-server / bank-core):**
    - Client calls an API endpoint to create an order.
    - The write service validates business rules and persists the new order in the write-side database (transactional).
    - After persisting, the write service **publishes a domain event** (for example: `OrderCreated` or `StockDecreaseRequest`) to a message broker (Kafka is used in related examples / patterns).

2. **Event bus / broker:**
    - The event is appended to a topic (or otherwise delivered to the message bus).

3. **Read-side (consumers / projections):**
    - One or more consumers subscribed to the event topic receive the event and update one or more **read-optimized stores** (projections) or cached views.
    - The read model(s) are designed/structured for serving queries efficiently and are independent of write-side schema and transactional constraints.

**Why this pattern is used here**
- Decouples high-throughput read workloads from transactional write workloads.
- Enables independent scaling and optimization of read models (e.g., projection tables, caches, NoSQL stores).
- Makes it easier to add analytic or denormalized views without impacting write transactions.

> Implementation note: the repository contains a `bank-event` module intended to contain event publishers and listeners. To see the exact event class names and topic names, inspect the `bank-event` and `bank-core` modules' source code.

---

## Distributed lock usage (Redisson / RLock)

The project uses Redis-based distributed locking (Redisson `RLock`) for critical sections where multiple instances could race on the same domain resource (for example, concurrent order processing or inventory updates). The distributed lock pattern used looks like this:

- Acquire an `RLock` with a `lockKey` derived from the domain resource (e.g. `account:{accountNumber}` or `order:{orderId}`).
- Use `tryLock(timeout, leaseTime, TimeUnit)` to attempt to obtain the lock with a wait timeout and a lease time (automatic expiration).
- If the lock is acquired, execute the critical business logic; in a `finally` block, check `lock.isHeldByCurrentThread()` and call `unlock()` to release the lock.
- The `leaseTime` acts as a safety net so the lock is automatically released if the holder crashes or fails to release it explicitly.

**Why distributed lock is used here**
- Prevents duplicate or conflicting updates when multiple service instances could try to update the same resource concurrently.
- Useful where distributed transactions or database-level row locking are not suitable or insufficient for cross-service coordination.

**Typical properties used** (example: see `LockProperties` in code):
- `timeout` — maximum time to wait for acquiring the lock (ms)
- `leaseTime` — how long the lock is held before automatic release (ms)
- `retryInterval` / `maxRetryAttempts` — optional retry strategy parameters for repeated lock attempts

---

## How to build

From the project root (where `gradlew` is located):

```bash
# Unix / macOS
./gradlew clean build

# Windows
gradlew.bat clean build
```

This will assemble all modules and run tests. If you prefer to build a single module, run `./gradlew :bank-api:build` (replace module name accordingly).

---

## How to run (development)

Most Spring Boot modules can be started with `bootRun` from their module directory or from the root by specifying the subproject:

```bash
# run API module
./gradlew :bank-api:bootRun
```

If the modules are designed to run independently (each a Spring Boot application), run the specific module you want to test. If the repo uses an application composition (gateway, config server, etc.), start those dependencies in order.

---

