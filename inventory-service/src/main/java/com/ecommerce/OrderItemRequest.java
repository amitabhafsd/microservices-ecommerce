package com.ecommerce;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemRequest {

    private UUID productId;

    private Integer quantity;

    private BigDecimal price;
}
