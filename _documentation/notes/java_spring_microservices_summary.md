# Java / Spring / Microservices LinkedIn Notes Summary

# 1. Synchronous vs Asynchronous Programming

## Synchronous (SYNC)
- Tasks execute one after another.
- Current thread waits until work finishes.
- Blocking operation.
- Easier to debug and understand.
- Best for:
  - Simple workflows
  - CPU-bound logic
  - Small applications

### Flow
```text
Task1 ---> Task2 ---> Task3
(Main thread waits)
```

## Asynchronous (ASYNC)
- Tasks can run independently.
- Non-blocking execution.
- Better scalability and responsiveness.
- Best for:
  - API calls
  - DB calls
  - File operations
  - High concurrency systems

### Flow
```text
Main Thread -----> Continue other work
        \
         ---> Background Task ---> Callback/Future
```

## Common Java Async Tools
- CompletableFuture
- ExecutorService
- ThreadPoolExecutor
- Reactive Streams
- Spring @Async

---

# 2. HashMap vs ConcurrentHashMap

## HashMap
- Not thread-safe
- Allows one null key and multiple null values
- Faster in single-threaded apps
- Uses array + linked list/tree internally

### Internal Structure
```text
Bucket Array
[0] -> Node -> Node
[1] -> Empty
[2] -> Node
```

## ConcurrentHashMap
- Thread-safe
- No null keys/values
- Better for multithreaded systems
- Uses locking/CAS internally

## Key Difference Table

| Feature | HashMap | ConcurrentHashMap |
|---|---|---|
| Thread Safe | No | Yes |
| Null Keys | Allowed | Not Allowed |
| Performance | Better single thread | Better concurrent |
| Use Case | Local cache | High concurrency |

---

# 3. Spring Boot Annotations Cheat Sheet

## Core Stereotypes
- @Component -> Generic bean
- @Service -> Business logic
- @Repository -> DAO layer
- @Controller -> MVC controller
- @RestController -> REST APIs

## Dependency Injection
- @Autowired
- @Qualifier
- @Primary
- @Value

## Configuration
- @Configuration
- @Bean
- @ComponentScan
- @ConfigurationProperties

## REST APIs
- @RequestMapping
- @GetMapping
- @PostMapping
- @PutMapping
- @DeleteMapping
- @RequestBody
- @PathVariable
- @RequestParam

## Validation
- @Valid
- @NotNull
- @Size
- @Email

## Transactions & AOP
- @Transactional
- @Aspect
- @Before
- @After
- @Around

## Testing
- @SpringBootTest
- @MockBean
- @WebMvcTest
- @DataJpaTest

---

# 4. Spring Framework Overview

## Main Concepts
- IoC (Inversion of Control)
- Dependency Injection
- AOP (Aspect Oriented Programming)
- Transaction Management
- MVC Architecture

## Spring Architecture
```text
Application
    |
Spring Framework
 ├── Web
 ├── AOP
 ├── Data Access
 ├── Security
 └── Core Container
```

## Bean Lifecycle
```text
Instantiate Bean
    ↓
Populate Properties
    ↓
PostConstruct
    ↓
Ready to Use
    ↓
PreDestroy
```

---

# 5. Strategy Pattern instead of Switch

## Problem with Huge switch-case
- Hard to maintain
- Violates Open/Closed Principle
- Difficult testing
- Tight coupling

## Strategy Pattern Solution

### Structure
```text
PaymentStrategy
    ↑
 ├── CardPaymentStrategy
 ├── UpiPaymentStrategy
 └── PaypalPaymentStrategy
```

## Benefits
- Cleaner code
- Easier extension
- Better testing
- Better separation of concerns

## Spring Boot Advantage
Spring can auto-inject strategies using:
```java
Map<String, PaymentStrategy>
```

---

# 6. Microservices Design Patterns

## Important Patterns

### Saga Pattern
- Handles distributed transactions.
- Uses compensation transactions.

### Circuit Breaker
- Stops repeated failures.
- Protects failing services.

### Retry with Backoff
- Retries with increasing delay.

### API Gateway
- Single entry point for clients.

### Idempotency
- Same request produces same result.

### Event Driven Architecture
- Services communicate via events.

### Bulkhead Pattern
- Isolates failures.

### Service Discovery
- Dynamic service lookup using Eureka/Consul.

## Microservice Flow ASCII
```text
Client
   |
API Gateway
   |
-------------------------
|     |        |        |
User Order Payment Inventory
Svc  Svc   Svc      Svc
```

---

# 7. Java Records

## What are Records?
- Immutable data carriers
- Less boilerplate
- Introduced in Java 16

## Example
```java
record User(String name, int age) {}
```

## Auto Generated
- Constructor
- Getters
- equals()
- hashCode()
- toString()

## Best Use Cases
- DTOs
- Kafka events
- API request/response
- Read-only models

## Avoid Records When
- Mutable state needed
- Complex business logic
- Inheritance required

---

# 8. Java Collection Framework

## Hierarchy ASCII
```text
Iterable
   |
Collection
 ├── List
 │    ├── ArrayList
 │    ├── LinkedList
 │    └── Vector
 ├── Set
 │    ├── HashSet
 │    ├── LinkedHashSet
 │    └── TreeSet
 └── Queue
      ├── PriorityQueue
      └── ArrayDeque

Map
 ├── HashMap
 ├── LinkedHashMap
 ├── TreeMap
 ├── Hashtable
 └── ConcurrentHashMap
```

## Quick Selection Guide

### Use ArrayList
- Frequent reads
- Random access

### Use LinkedList
- Frequent insert/delete

### Use HashSet
- Unique items
- Fast lookup

### Use TreeSet / TreeMap
- Sorted data

### Use ConcurrentHashMap
- Multi-threaded applications

---

# 9. Java 8 Features

## Lambda Expressions
```java
list.forEach(name -> System.out.println(name));
```

## Stream API
```java
names.stream()
     .filter(n -> n.length() > 3)
     .map(String::toUpperCase)
     .toList();
```

## Functional Interfaces
- Single abstract method
- Example:
  - Runnable
  - Comparator
  - Consumer
  - Supplier

## Optional
- Avoids NullPointerException

## Default Methods
- Interface methods with implementation

## Date & Time API
- LocalDate
- LocalDateTime
- ZonedDateTime

---

# Final Interview Preparation Notes

## Most Important Topics
1. Spring Boot annotations
2. Collections framework
3. Java 8 features
4. Microservices patterns
5. Async programming
6. ConcurrentHashMap
7. Strategy pattern
8. Java records

## Recommended Learning Order
```text
Java Basics
   ↓
Collections
   ↓
Java 8
   ↓
Spring Core
   ↓
Spring Boot
   ↓
REST APIs
   ↓
Microservices
   ↓
Reactive Programming
```

## Practical Advice
- Learn concepts with hands-on coding.
- Focus on real-world use cases.
- Understand WHY, not only syntax.
- Practice debugging and design patterns.
- Build mini projects for every topic.

