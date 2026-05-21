package com.ecommerce;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {

    private UUID orderId;

    private String status;

    private BigDecimal totalAmount;
}
