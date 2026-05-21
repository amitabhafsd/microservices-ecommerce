package com.ecommerce;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class InventoryReservedEvent {

    private UUID orderId;

    private BigDecimal amount;
}
