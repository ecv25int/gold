package com.goldapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "gold_inventory")
public class GoldInventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private Integer carat; // 10, 14, 18, 22, 24
    
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(precision = 19, scale = 6, nullable = false)
    private BigDecimal quantityInGrams = BigDecimal.ZERO;
    
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal averageBuyPrice = BigDecimal.ZERO; // Average price paid per gram
    
    @DecimalMin(value = "0.0")
    @Column(precision = 19, scale = 2)
    private BigDecimal minimumStock = BigDecimal.ZERO; // Alert threshold
    
    @DecimalMin(value = "0.0")
    @Column(precision = 19, scale = 2)
    private BigDecimal maximumStock = BigDecimal.valueOf(1000); // Maximum capacity
    
    private LocalDateTime lastUpdated;
    
    @Column(length = 500)
    private String notes;
    
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryMovement> movements;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
    
    // Constructors
    public GoldInventory() {}
    
    public GoldInventory(Integer carat, BigDecimal quantityInGrams, BigDecimal averageBuyPrice) {
        this.carat = carat;
        this.quantityInGrams = quantityInGrams;
        this.averageBuyPrice = averageBuyPrice;
    }
    
    // Business methods
    public void addStock(BigDecimal quantity, BigDecimal pricePerGram) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        // Calculate new average buy price
        BigDecimal currentValue = this.quantityInGrams.multiply(this.averageBuyPrice);
        BigDecimal addedValue = quantity.multiply(pricePerGram);
        BigDecimal totalQuantity = this.quantityInGrams.add(quantity);
        
        if (totalQuantity.compareTo(BigDecimal.ZERO) > 0) {
            this.averageBuyPrice = currentValue.add(addedValue).divide(totalQuantity, 2, RoundingMode.HALF_UP);
        }
        
        this.quantityInGrams = totalQuantity;
    }
    
    public void removeStock(BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        if (this.quantityInGrams.compareTo(quantity) < 0) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + this.quantityInGrams + ", Requested: " + quantity);
        }
        
        this.quantityInGrams = this.quantityInGrams.subtract(quantity);
    }
    
    public boolean isLowStock() {
        return quantityInGrams.compareTo(minimumStock) <= 0;
    }
    
    public boolean isOverStock() {
        return quantityInGrams.compareTo(maximumStock) > 0;
    }
    
    public BigDecimal getInventoryValue() {
        return quantityInGrams.multiply(averageBuyPrice);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getCarat() { return carat; }
    public void setCarat(Integer carat) { this.carat = carat; }
    
    public BigDecimal getQuantityInGrams() { return quantityInGrams; }
    public void setQuantityInGrams(BigDecimal quantityInGrams) { this.quantityInGrams = quantityInGrams; }
    
    public BigDecimal getAverageBuyPrice() { return averageBuyPrice; }
    public void setAverageBuyPrice(BigDecimal averageBuyPrice) { this.averageBuyPrice = averageBuyPrice; }
    
    public BigDecimal getMinimumStock() { return minimumStock; }
    public void setMinimumStock(BigDecimal minimumStock) { this.minimumStock = minimumStock; }
    
    public BigDecimal getMaximumStock() { return maximumStock; }
    public void setMaximumStock(BigDecimal maximumStock) { this.maximumStock = maximumStock; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<InventoryMovement> getMovements() { return movements; }
    public void setMovements(List<InventoryMovement> movements) { this.movements = movements; }
}