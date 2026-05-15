# INVENTORY SERVICE

This is one of the MOST IMPORTANT services in the entire architecture:
This service is critical because:

* stock consistency matters
* Saga transaction starts here
* payment failures need stock rollback
* concurrency issues happen here

**This is where real distributed transaction concepts begin.**

## RESPONSIBILITIES

### Inventory Service handles:

* stock validation
* stock reservation
* stock deduction
* stock release
* inventory management


### SAGA ROLE

```
ORDER CREATED
    |
    v
Inventory reserves stock
    |
    v
Payment starts
    |
    +---- SUCCESS -> confirm stock
    |
    +---- FAILURE -> release stock
```

This is:

* eventual consistency
* distributed transaction management