package com.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class InventoryReservedConsumer {

    private final PaymentService paymentService;

    private final PaymentKafkaProducer producer;

    @KafkaListener(topics = "inventory-reserved-topic")
    public void consume(InventoryReservedEvent event) {

        // Dummy amount for now
        BigDecimal amount = BigDecimal.valueOf(1000);

        boolean success = paymentService.processPayment(event.getOrderId(), amount);

        if (success) {

            producer.publishPaymentSuccess(PaymentSuccessEvent
                    .builder()
                    .orderId(event.getOrderId())
                    .build());

        } else {

            producer.publishPaymentFailed(PaymentFailedEvent
                    .builder()
                    .orderId(event.getOrderId())
                    .reason("Payment failed")
                    .build());
        }
    }
}
