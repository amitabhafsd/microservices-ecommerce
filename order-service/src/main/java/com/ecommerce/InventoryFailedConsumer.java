package com.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryFailedConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "inventory-failed-topic")
    public void consume(InventoryFailedEvent event) {

        orderService.markOrderCancelled(event.getOrderId());
    }
}
