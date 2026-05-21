package com.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse>
    createOrder(
            @RequestBody
            CreateOrderRequest request) {

        return ResponseEntity.ok(
                orderService.createOrder(request)
        );
    }
}


/*

Payload  =

{
  "userId": "11111111-2222-3333-4444-555555555555",
  "items": [
    {
      "productId": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
      "quantity": 2,
      "price": 1200
    }
  ]
}



* */
