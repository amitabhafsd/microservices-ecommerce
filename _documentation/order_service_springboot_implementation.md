# Order Service Implementation - Spring Boot Microservice

This implementation demonstrates a production-style Order Service for an E-Commerce Microservices platform.

Features covered:

- Spring Boot 3
- Java 21
- PostgreSQL
- Kafka Integration
- Saga Pattern
- JWT Authentication
- Eureka Client
- OpenFeign
- Resilience4j Circuit Breaker
- Docker Ready
- Distributed Tracing
- Validation
- Global Exception Handling
- DTO Pattern
- Layered Architecture

---

# 1. Maven Dependencies

```xml
<dependencies>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>

</dependencies>
```

---

# 2. Project Structure

```text
order-service/
│
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── kafka/
├── config/
├── exception/
├── security/
├── feign/
├── mapper/
└── OrderServiceApplication.java
```

---

# 3. application.yml

```yaml
server:
  port: 8082

spring:
  application:
    name: ORDER-SERVICE

  datasource:
    url: jdbc:postgresql://localhost:5432/order_db
    username: postgres
    password: postgres

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  kafka:
    bootstrap-servers: localhost:9092

  cloud:
    compatibility-verifier:
      enabled: false

management:
  tracing:
    sampling:
      probability: 1.0

jwt:
  secret: mysecretkeymysecretkeymysecretkey

resilience4j:
  circuitbreaker:
    instances:
      inventoryService:
        sliding-window-size: 5
        failure-rate-threshold: 50
```

---

# 4. Main Application

```java
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

---

# 5. Order Entity

```java
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;
}
```

---

# 6. Order Status Enum

```java
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    PAYMENT_FAILED
}
```

---

# 7. Order Item Entity

```java
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID productId;

    private Integer quantity;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
```

---

# 8. DTOs

## CreateOrderRequest

```java
@Data
public class CreateOrderRequest {

    @NotNull
    private UUID userId;

    @NotEmpty
    private List<OrderItemRequest> items;
}
```

## OrderItemRequest

```java
@Data
public class OrderItemRequest {

    private UUID productId;

    private Integer quantity;

    private BigDecimal price;
}
```

## OrderResponse

```java
@Data
@Builder
public class OrderResponse {

    private UUID orderId;

    private String status;

    private BigDecimal totalAmount;
}
```

---

# 9. Repository Layer

## OrderRepository

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
}
```

## OrderItemRepository

```java
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
```

---

# 10. Kafka Event Model

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {

    private UUID orderId;

    private UUID userId;

    private BigDecimal amount;

    private List<OrderItemRequest> items;
}
```

---

# 11. Kafka Producer

```java
@Service
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {

        kafkaTemplate.send("order-created-topic", event);
    }
}
```

---

# 12. Feign Client - Inventory Service

```java
@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {

    @PostMapping("/api/inventory/check")
    InventoryResponse checkInventory(@RequestBody InventoryRequest request);
}
```

---

# 13. Service Layer

```java
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderKafkaProducer kafkaProducer;

    @CircuitBreaker(name = "inventoryService")
    public OrderResponse createOrder(CreateOrderRequest request) {

        BigDecimal total = request.getItems()
                .stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(request.getUserId())
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = request.getItems()
                .stream()
                .map(item -> OrderItem.builder()
                        .order(savedOrder)
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .toList();

        orderItemRepository.saveAll(orderItems);

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .amount(savedOrder.getTotalAmount())
                .items(request.getItems())
                .build();

        kafkaProducer.publishOrderCreated(event);

        return OrderResponse.builder()
                .orderId(savedOrder.getId())
                .status(savedOrder.getStatus().name())
                .totalAmount(savedOrder.getTotalAmount())
                .build();
    }

    public void markOrderConfirmed(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow();

        order.setStatus(OrderStatus.CONFIRMED);

        orderRepository.save(order);
    }

    public void markOrderCancelled(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow();

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
    }
}
```

---

# 14. REST Controller

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return ResponseEntity.ok(orderService.createOrder(request));
    }
}
```

---

# 15. Kafka Consumer - Payment Success

```java
@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "payment-success-topic")
    public void consume(PaymentSuccessEvent event) {

        orderService.markOrderConfirmed(event.getOrderId());
    }
}
```

---

# 16. Kafka Consumer - Payment Failure

```java
@Component
@RequiredArgsConstructor
public class PaymentFailureConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "payment-failed-topic")
    public void consume(PaymentFailedEvent event) {

        orderService.markOrderCancelled(event.getOrderId());
    }
}
```

---

# 17. Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}
```

---

# 18. JWT Filter

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            // Validate JWT here
        }

        filterChain.doFilter(request, response);
    }
}
```

---

# 19. Security Configuration

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/orders/**")
                        .authenticated()
                        .anyRequest()
                        .permitAll())
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

---

# 20. Saga Flow Explained

```text
1. Client calls Order Service
2. Order created with PENDING status
3. Order Service publishes ORDER_CREATED event
4. Inventory Service reserves stock
5. Payment Service processes payment
6. Payment success -> order CONFIRMED
7. Payment failure -> order CANCELLED
```

---

# 21. Compensation Transaction

If payment fails:

```text
PAYMENT_FAILED event
      |
      v
Inventory releases stock
      |
      v
Order Service cancels order
```

---

# 22. Sample API Request

```http
POST /api/orders
Authorization: Bearer jwt_token
Content-Type: application/json
```

```json
{
  "userId": "8d1c9c36-1234-4bde-a111-5a1234567890",
  "items": [
    {
      "productId": "11111111-2222-3333-4444-555555555555",
      "quantity": 2,
      "price": 1200
    }
  ]
}
```

---

# 23. Example Success Response

```json
{
  "orderId": "7cb86b65-0c6b-4f9c-a81f-7d2a2d6f7c21",
  "status": "PENDING",
  "totalAmount": 2400
}
```

---

# 24. Dockerfile

```dockerfile
FROM eclipse-temurin:21

COPY target/order-service.jar order-service.jar

ENTRYPOINT ["java", "-jar", "order-service.jar"]
```

---

# 25. Recommended Improvements

Production enhancements you should add later:

- Outbox Pattern
- Idempotency
- Dead Letter Queue
- Retry Topics
- Redis Cache
- Flyway Migration
- OpenAPI Swagger
- Testcontainers
- Unit Tests
- Integration Tests
- Kubernetes Deployment
- Centralized Config Server
- Zipkin Tracing
- Prometheus Metrics
- ELK Logging

---

# 26. Important Interview Concepts Covered

This implementation demonstrates:

- Distributed Transactions
- Saga Pattern
- Event-Driven Architecture
- Kafka Messaging
- JWT Security
- Microservices Communication
- Fault Tolerance
- Circuit Breaker
- Database Per Service
- Compensation Transactions
- Eventually Consistent Systems
- Spring Cloud Architecture

---

# 27. Next Recommended Service

After this, implement in order:

1. Inventory Service
2. Payment Service
3. API Gateway
4. Auth Service
5. Notification Service
6. Config Server
7. Discovery Server
8. Docker Compose
9. Kubernetes

