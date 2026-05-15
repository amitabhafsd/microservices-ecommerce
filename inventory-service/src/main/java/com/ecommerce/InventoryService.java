package com.ecommerce;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryKafkaProducer  inventoryKafkaProducer;

    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public List<Inventory> createInventoryBulk(List<Inventory> inventoryList) {
        return inventoryRepository.saveAll(inventoryList);
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    /*@Transactional
    public void reserveInventoryForOrder(List<OrderItemRequest> items) {
        for (OrderItemRequest item : items) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProductId()).orElseThrow();

            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                throw new InsufficientInventoryException("Out of stock");
            }

            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - item.getQuantity());

            inventory.setReservedQuantity(inventory.getReservedQuantity() + item.getQuantity());

            inventoryRepository.save(inventory);
        }
    }*/

    public boolean reserveInventory(UUID productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow();
        if (inventory.getAvailableQuantity() < quantity) {
            return false;
        }
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventoryRepository.save(inventory);
        return true;
    }

    public void releaseInventory(UUID productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow();
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventoryRepository.save(inventory);
    }
}
