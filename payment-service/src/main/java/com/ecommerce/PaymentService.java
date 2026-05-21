package com.ecommerce;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public boolean processPayment(UUID orderId, BigDecimal amount) {

        // SIMULATION
        // Later integrate Stripe/Razorpay/etc

        boolean success = new Random().nextBoolean();

        Payment payment = Payment
                .builder()
                .orderId(orderId)
                .amount(amount)
                .paymentMethod("CARD")
                .createdAt(LocalDateTime.now())
                .status(success
                        ? PaymentStatus.SUCCESS
                        : PaymentStatus.FAILED)
                .build();

        paymentRepository.save(payment);

        return success;
    }
}
