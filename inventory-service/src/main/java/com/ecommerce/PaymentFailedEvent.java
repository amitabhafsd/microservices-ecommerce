package com.ecommerce;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PaymentFailedEvent {

    private UUID orderId;

    private List<OrderItemRequest> items;

    private String reason;
}
