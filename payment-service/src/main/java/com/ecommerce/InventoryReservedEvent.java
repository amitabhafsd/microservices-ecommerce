package com.ecommerce;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class InventoryReservedEvent {

    private UUID orderId;

    private List<OrderItemRequest> items;

    private BigDecimal amount;
}
