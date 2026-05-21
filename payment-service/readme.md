# PAYMENT SERVICE

Now we build the service that decides the FINAL outcome of the Saga.

This is one of the most critical services because:

* payment success → order confirmed
* payment failure → compensation triggered
* inventory rollback depends on this
* order state depends on this

This service effectively controls distributed transaction completion.

### SAGA FLOW NOW
```
Order Created
    |
    v
Inventory Reserved
    |
    v
Payment Processing
    |
    +---- SUCCESS
    |        |
    |        v
    |   Order Confirmed
    |
    +---- FAILURE
            |
            v
    Inventory Released
            |
            v
        Order Cancelled
```
### RESPONSIBILITIES

Payment Service handles:

* payment processing
* success/failure decision
* payment status tracking
* payment events
* Saga continuation



COMPLETE SAGA FLOW NOW

```
Order Service
      |
      v
ORDER_CREATED
      |
      v
Inventory Service
      |
      +---- reserve stock
      |
      v
INVENTORY_RESERVED
      |
      v
Payment Service
      |
      +---- SUCCESS
      |         |
      |         v
      |    PAYMENT_SUCCESS
      |
      +---- FAILURE
                |
                v
         PAYMENT_FAILED
```
