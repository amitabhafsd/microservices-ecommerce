package com.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "payment-failed-topic")
    public void consume(PaymentFailedEvent event) {

        for (OrderItemRequest item : event.getItems()) {

            inventoryService.releaseInventory(item.getProductId(), item.getQuantity());
        }
    }
}
