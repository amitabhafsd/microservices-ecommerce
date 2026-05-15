package com.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final InventoryService inventoryService;

    private final InventoryKafkaProducer producer;

    @KafkaListener(topics = "order-created-topic")
    public void consume(OrderCreatedEvent event) {

        boolean allAvailable = true;

        for (OrderItemRequest item : event.getItems()) {
            boolean reserved = inventoryService.reserveInventory(item.getProductId(), item.getQuantity());
            if (!reserved) {
                allAvailable = false;
                break;
            }
        }

        if (allAvailable) {
            producer.publishInventoryReserved(InventoryReservedEvent
                    .builder()
                    .orderId(event.getOrderId())
                    .build());
        } else {
            producer.publishInventoryFailed(InventoryFailedEvent
                    .builder()
                    .orderId(event.getOrderId())
                    .reason("Out of stock")
                    .build());
        }
    }

    /*@KafkaListener(topics = "order-created-topic")
    public void consume(OrderCreatedEvent event) {
        try {
            inventoryService.reserveInventoryForOrder(event.getItems());

            producer.publishInventoryReserved(InventoryReservedEvent
                    .builder()
                    .orderId(event.getOrderId())
                    .build());

        } catch (InsufficientInventoryException ex) {
            producer.publishInventoryFailed(InventoryFailedEvent
                    .builder()
                    .orderId(event.getOrderId())
                    .reason("Out of stock")
                    .build());
        }
    }*/
}
