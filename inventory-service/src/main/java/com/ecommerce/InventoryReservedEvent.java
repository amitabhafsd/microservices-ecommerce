package com.ecommerce;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class InventoryReservedEvent {

    private UUID orderId;
}
