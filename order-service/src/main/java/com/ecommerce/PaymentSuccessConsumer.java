package com.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "payment-success-topic"
    )
    public void consume(
            PaymentSuccessEvent event) {

        orderService.markOrderConfirmed(
                event.getOrderId()
        );
    }
}
