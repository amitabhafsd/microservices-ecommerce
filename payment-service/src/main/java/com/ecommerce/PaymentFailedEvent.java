package com.ecommerce;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PaymentFailedEvent {

    private UUID orderId;

    private String reason;
}
