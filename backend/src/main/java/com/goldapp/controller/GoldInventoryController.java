package com.goldapp.controller;

import com.goldapp.entity.GoldInventory;
import com.goldapp.entity.InventoryMovement;
import com.goldapp.service.GoldInventoryService;
import com.goldapp.repository.InventoryMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GoldInventoryController {
    
    @Autowired
    private GoldInventoryService inventoryService;
    
    @Autowired
    private InventoryMovementRepository movementRepository;
    
    @PostMapping
    public ResponseEntity<?> createInventory(@Valid @RequestBody GoldInventory inventory) {
        try {
            GoldInventory createdInventory = inventoryService.createInventory(inventory);
            return ResponseEntity.ok(createdInventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory(@PathVariable Long id, @Valid @RequestBody GoldInventory inventory) {
        try {
            GoldInventory updatedInventory = inventoryService.updateInventory(id, inventory);
            return ResponseEntity.ok(updatedInventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getInventory(@PathVariable Long id) {
        try {
            GoldInventory inventory = inventoryService.getInventoryById(id);
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/carat/{carat}")
    public ResponseEntity<?> getInventoryByCarat(@PathVariable Integer carat) {
        return inventoryService.getInventoryByCarat(carat)
                .map(inventory -> ResponseEntity.ok(inventory))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<GoldInventory>> getAllInventory() {
        List<GoldInventory> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventory);
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<GoldInventory>> getLowStockItems() {
        List<GoldInventory> lowStockItems = inventoryService.getLowStockItems();
        return ResponseEntity.ok(lowStockItems);
    }
    
    @GetMapping("/over-stock")
    public ResponseEntity<List<GoldInventory>> getOverStockItems() {
        List<GoldInventory> overStockItems = inventoryService.getOverStockItems();
        return ResponseEntity.ok(overStockItems);
    }
    
    @GetMapping("/summary")
    public ResponseEntity<?> getInventorySummary() {
        try {
            BigDecimal totalValue = inventoryService.getTotalInventoryValue();
            BigDecimal totalQuantity = inventoryService.getTotalGoldQuantity();
            List<GoldInventory> lowStock = inventoryService.getLowStockItems();
            List<GoldInventory> overStock = inventoryService.getOverStockItems();
            
            Map<String, Object> summary = Map.of(
                "totalValue", totalValue,
                "totalQuantity", totalQuantity,
                "lowStockCount", lowStock.size(),
                "overStockCount", overStock.size(),
                "lowStockItems", lowStock,
                "overStockItems", overStock
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/{inventoryId}/adjust")
    public ResponseEntity<?> adjustStock(
            @PathVariable Long inventoryId,
            @RequestParam BigDecimal newQuantity,
            @RequestParam String reason) {
        try {
            inventoryService.adjustStock(inventoryId, newQuantity, reason);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/check-stock/{carat}")
    public ResponseEntity<?> checkStock(@PathVariable Integer carat, @RequestParam BigDecimal quantity) {
        try {
            boolean hasStock = inventoryService.hasAvailableStock(carat, quantity);
            BigDecimal availableStock = inventoryService.getAvailableStock(carat);
            
            Map<String, Object> stockInfo = Map.of(
                "hasStock", hasStock,
                "availableStock", availableStock,
                "requestedQuantity", quantity
            );
            
            return ResponseEntity.ok(stockInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{inventoryId}/movements")
    public ResponseEntity<Page<InventoryMovement>> getInventoryMovements(
            @PathVariable Long inventoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            GoldInventory inventory = inventoryService.getInventoryById(inventoryId);
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<InventoryMovement> movements = movementRepository.findByInventoryOrderByCreatedAtDesc(inventory, pageable);
            return ResponseEntity.ok(movements);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/movements")
    public ResponseEntity<Page<InventoryMovement>> getAllMovements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InventoryMovement> movements = movementRepository.findAllOrderByCreatedAtDesc(pageable);
        return ResponseEntity.ok(movements);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long id) {
        try {
            inventoryService.deleteInventory(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}