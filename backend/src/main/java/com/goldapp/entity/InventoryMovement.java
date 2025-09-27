package com.goldapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private GoldInventory inventory;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private MovementType movementType;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(precision = 19, scale = 6)
    private BigDecimal quantity;
    
    @DecimalMin(value = "0.0")
    @Column(precision = 19, scale = 2)
    private BigDecimal pricePerGram;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal balanceAfter; // Inventory balance after this movement
    
    @Column(length = 500)
    private String reason;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public InventoryMovement() {}
    
    public InventoryMovement(GoldInventory inventory, MovementType movementType, 
                           BigDecimal quantity, BigDecimal pricePerGram, String reason) {
        this.inventory = inventory;
        this.movementType = movementType;
        this.quantity = quantity;
        this.pricePerGram = pricePerGram;
        this.reason = reason;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public GoldInventory getInventory() { return inventory; }
    public void setInventory(GoldInventory inventory) { this.inventory = inventory; }
    
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    
    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    
    public BigDecimal getPricePerGram() { return pricePerGram; }
    public void setPricePerGram(BigDecimal pricePerGram) { this.pricePerGram = pricePerGram; }
    
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public enum MovementType {
        IN,     // Stock increase (buying from providers)
        OUT,    // Stock decrease (selling to clients)
        ADJUSTMENT_IN,   // Manual stock increase
        ADJUSTMENT_OUT   // Manual stock decrease
    }
}