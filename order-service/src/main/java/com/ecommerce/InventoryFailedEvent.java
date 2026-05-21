package com.ecommerce;

import lombok.Data;

import java.util.UUID;

@Data
public class InventoryFailedEvent {

    private UUID orderId;

    private String reason;
}
