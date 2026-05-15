package com.ecommerce;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderCreatedEvent {

    private UUID orderId;

    private UUID userId;

    private BigDecimal amount;

    private List<OrderItemRequest> items;
}
