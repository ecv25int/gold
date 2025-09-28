package com.goldapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gold_prices")
public class GoldPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(precision = 19, scale = 2)
    private BigDecimal buyPrice;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(precision = 19, scale = 2)
    private BigDecimal sellPrice;
    
    @Column(length = 10)
    private String currency = "USD";
    
    // Removed unit field; all prices are per gram
    
    @Column(updatable = false)
    private LocalDateTime timestamp;
    
    private boolean isActive = true;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
    
    // Constructors
    public GoldPrice() {}
    
    public GoldPrice(BigDecimal buyPrice, BigDecimal sellPrice) {
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BigDecimal getBuyPrice() { return buyPrice; }
    public void setBuyPrice(BigDecimal buyPrice) { this.buyPrice = buyPrice; }
    
    public BigDecimal getSellPrice() { return sellPrice; }
    public void setSellPrice(BigDecimal sellPrice) { this.sellPrice = sellPrice; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    // Removed getUnit(); unit field no longer exists
    // Removed unit getter/setter
    
    public LocalDateTime getTimestamp() { return timestamp; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}