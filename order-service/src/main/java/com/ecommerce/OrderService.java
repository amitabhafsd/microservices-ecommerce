package com.ecommerce;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository
            orderItemRepository;

    private final OrderKafkaProducer producer;

    public OrderResponse createOrder(
            CreateOrderRequest request) {

        BigDecimal totalAmount =
                request.getItems()
                        .stream()
                        .map(item ->
                                item.getPrice()
                                        .multiply(
                                                BigDecimal.valueOf(
                                                        item.getQuantity()
                                                )
                                        )
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        Order order = Order.builder()
                .userId(request.getUserId())
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder =
                orderRepository.save(order);

        List<OrderItem> orderItems =
                request.getItems()
                        .stream()
                        .map(item ->
                                OrderItem.builder()
                                        .order(savedOrder)
                                        .productId(
                                                item.getProductId()
                                        )
                                        .quantity(
                                                item.getQuantity()
                                        )
                                        .price(
                                                item.getPrice()
                                        )
                                        .build()
                        )
                        .toList();

        orderItemRepository.saveAll(orderItems);

        producer.publishOrderCreated(
                OrderCreatedEvent.builder()
                        .orderId(savedOrder.getId())
                        .userId(savedOrder.getUserId())
                        .amount(
                                savedOrder.getTotalAmount()
                        )
                        .items(request.getItems())
                        .build()
        );

        return OrderResponse.builder()
                .orderId(savedOrder.getId())
                .status(
                        savedOrder.getStatus().name()
                )
                .totalAmount(
                        savedOrder.getTotalAmount()
                )
                .build();
    }

    public void markOrderConfirmed(
            UUID orderId) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow();

        order.setStatus(
                OrderStatus.CONFIRMED
        );

        orderRepository.save(order);
    }

    public void markOrderCancelled(
            UUID orderId) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow();

        order.setStatus(
                OrderStatus.CANCELLED
        );

        orderRepository.save(order);
    }
}