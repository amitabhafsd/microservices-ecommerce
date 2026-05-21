package com.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryKafkaProducer {

    private final KafkaTemplate<String, Object>
            kafkaTemplate;

    public void publishInventoryReserved(InventoryReservedEvent event) {
        InventoryReservedEvent toBePublished = InventoryReservedEvent.builder()
                .orderId(event.getOrderId())
                .items(event.getItems())
                .amount(event.getAmount())
                .build();

        kafkaTemplate.send("inventory-reserved-topic", toBePublished);
    }

    public void publishInventoryFailed(InventoryFailedEvent event) {
        kafkaTemplate.send("inventory-failed-topic", event);
    }
}
