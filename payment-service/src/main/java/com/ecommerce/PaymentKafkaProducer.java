package com.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentSuccess(PaymentSuccessEvent event) {
        kafkaTemplate.send("payment-success-topic", event);
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        PaymentFailedEvent toBePublished = PaymentFailedEvent.builder()
                .orderId(event.getOrderId())
                .items(event.getItems())
                .reason("Payment failed")
                .build();
        kafkaTemplate.send("payment-failed-topic", toBePublished);
    }
}
