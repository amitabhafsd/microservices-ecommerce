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
        kafkaTemplate.send("inventory-reserved-topic", event);
    }

    public void publishInventoryFailed(InventoryFailedEvent event) {
        kafkaTemplate.send("inventory-failed-topic", event);
    }
}
