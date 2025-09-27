package com.goldapp.service;

import com.goldapp.entity.GoldInventory;
import com.goldapp.entity.InventoryMovement;
import com.goldapp.entity.Transaction;
import com.goldapp.repository.GoldInventoryRepository;
import com.goldapp.repository.InventoryMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GoldInventoryService {
    
    @Autowired
    private GoldInventoryRepository inventoryRepository;
    
    @Autowired
    private InventoryMovementRepository movementRepository;
    
    public GoldInventory createInventory(GoldInventory inventory) {
        validateInventory(inventory);
        
        if (inventoryRepository.existsByCarat(inventory.getCarat())) {
            throw new RuntimeException("Inventory for " + inventory.getCarat() + " carat gold already exists");
        }
        
        return inventoryRepository.save(inventory);
    }
    
    public GoldInventory updateInventory(Long id, GoldInventory inventoryDetails) {
        GoldInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + id));
        
        validateInventory(inventoryDetails);
        
        // Check if carat is being changed and if new carat already exists
        if (!inventory.getCarat().equals(inventoryDetails.getCarat()) && 
            inventoryRepository.existsByCarat(inventoryDetails.getCarat())) {
            throw new RuntimeException("Inventory for " + inventoryDetails.getCarat() + " carat gold already exists");
        }
        
        inventory.setCarat(inventoryDetails.getCarat());
        inventory.setMinimumStock(inventoryDetails.getMinimumStock());
        inventory.setMaximumStock(inventoryDetails.getMaximumStock());
        inventory.setNotes(inventoryDetails.getNotes());
        
        return inventoryRepository.save(inventory);
    }
    
    public GoldInventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + id));
    }
    
    public Optional<GoldInventory> getInventoryByCarat(Integer carat) {
        return inventoryRepository.findByCarat(carat);
    }
    
    public List<GoldInventory> getAllInventory() {
        return inventoryRepository.findAllByOrderByCaratAsc();
    }
    
    public List<GoldInventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }
    
    public List<GoldInventory> getOverStockItems() {
        return inventoryRepository.findOverStockItems();
    }
    
    public BigDecimal getTotalInventoryValue() {
        BigDecimal total = inventoryRepository.getTotalInventoryValue();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalGoldQuantity() {
        BigDecimal total = inventoryRepository.getTotalGoldQuantity();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Transactional
    public void addStock(Integer carat, BigDecimal quantity, BigDecimal pricePerGram, 
                        String reason, Transaction transaction) {
        validateStockOperation(quantity, pricePerGram);
        
        GoldInventory inventory = getOrCreateInventory(carat);
        
        inventory.addStock(quantity, pricePerGram);
        inventoryRepository.save(inventory);
        
        // Record movement
        InventoryMovement movement = new InventoryMovement(
                inventory, 
                InventoryMovement.MovementType.IN, 
                quantity, 
                pricePerGram, 
                reason
        );
        movement.setTransaction(transaction);
        movement.setBalanceAfter(inventory.getQuantityInGrams());
        movementRepository.save(movement);
    }
    
    @Transactional
    public void removeStock(Integer carat, BigDecimal quantity, String reason, Transaction transaction) {
        validateStockOperation(quantity, null);
        
        GoldInventory inventory = inventoryRepository.findByCarat(carat)
                .orElseThrow(() -> new RuntimeException("No inventory found for " + carat + " carat gold"));
        
        inventory.removeStock(quantity);
        inventoryRepository.save(inventory);
        
        // Record movement - use current average price for record keeping
        InventoryMovement movement = new InventoryMovement(
                inventory, 
                InventoryMovement.MovementType.OUT, 
                quantity, 
                inventory.getAverageBuyPrice(), 
                reason
        );
        movement.setTransaction(transaction);
        movement.setBalanceAfter(inventory.getQuantityInGrams());
        movementRepository.save(movement);
    }
    
    @Transactional
    public void adjustStock(Long inventoryId, BigDecimal newQuantity, String reason) {
        GoldInventory inventory = getInventoryById(inventoryId);
        BigDecimal currentQuantity = inventory.getQuantityInGrams();
        BigDecimal difference = newQuantity.subtract(currentQuantity);
        
        if (difference.compareTo(BigDecimal.ZERO) == 0) {
            return; // No change needed
        }
        
        inventory.setQuantityInGrams(newQuantity);
        inventoryRepository.save(inventory);
        
        // Record adjustment movement
        InventoryMovement.MovementType movementType = difference.compareTo(BigDecimal.ZERO) > 0 ?
                InventoryMovement.MovementType.ADJUSTMENT_IN : InventoryMovement.MovementType.ADJUSTMENT_OUT;
        
        InventoryMovement movement = new InventoryMovement(
                inventory, 
                movementType, 
                difference.abs(), 
                inventory.getAverageBuyPrice(), 
                reason
        );
        movement.setBalanceAfter(inventory.getQuantityInGrams());
        movementRepository.save(movement);
    }
    
    public boolean hasAvailableStock(Integer carat, BigDecimal requiredQuantity) {
        Optional<GoldInventory> inventory = inventoryRepository.findByCarat(carat);
        return inventory.isPresent() && 
               inventory.get().getQuantityInGrams().compareTo(requiredQuantity) >= 0;
    }
    
    public BigDecimal getAvailableStock(Integer carat) {
        return inventoryRepository.findByCarat(carat)
                .map(GoldInventory::getQuantityInGrams)
                .orElse(BigDecimal.ZERO);
    }
    
    public void deleteInventory(Long id) {
        GoldInventory inventory = getInventoryById(id);
        
        if (inventory.getQuantityInGrams().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot delete inventory with remaining stock");
        }
        
        inventoryRepository.deleteById(id);
    }
    
    private GoldInventory getOrCreateInventory(Integer carat) {
        return inventoryRepository.findByCarat(carat)
                .orElseGet(() -> {
                    GoldInventory newInventory = new GoldInventory();
                    newInventory.setCarat(carat);
                    newInventory.setQuantityInGrams(BigDecimal.ZERO);
                    newInventory.setAverageBuyPrice(BigDecimal.ZERO);
                    return inventoryRepository.save(newInventory);
                });
    }
    
    private void validateInventory(GoldInventory inventory) {
        if (inventory.getCarat() == null || inventory.getCarat() <= 0) {
            throw new RuntimeException("Valid carat value is required");
        }
        
        List<Integer> validCarats = List.of(10, 14, 18, 22, 24);
        if (!validCarats.contains(inventory.getCarat())) {
            throw new RuntimeException("Carat must be one of: " + validCarats);
        }
        
        if (inventory.getMinimumStock() != null && inventory.getMinimumStock().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Minimum stock cannot be negative");
        }
        
        if (inventory.getMaximumStock() != null && inventory.getMaximumStock().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Maximum stock cannot be negative");
        }
        
        if (inventory.getMinimumStock() != null && inventory.getMaximumStock() != null &&
            inventory.getMinimumStock().compareTo(inventory.getMaximumStock()) > 0) {
            throw new RuntimeException("Minimum stock cannot be greater than maximum stock");
        }
    }
    
    private void validateStockOperation(BigDecimal quantity, BigDecimal pricePerGram) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantity must be positive");
        }
        
        if (pricePerGram != null && pricePerGram.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Price per gram cannot be negative");
        }
    }
}