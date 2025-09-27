package com.goldapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "carat_prices")
public class CaratPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int carat; // e.g. 14, 18, 22, 24

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal pricePerGram;

    public CaratPrice() {}
    public CaratPrice(int carat, BigDecimal pricePerGram) {
        this.carat = carat;
        this.pricePerGram = pricePerGram;
    }
    public Long getId() { return id; }
    public int getCarat() { return carat; }
    public void setCarat(int carat) { this.carat = carat; }
    public BigDecimal getPricePerGram() { return pricePerGram; }
    public void setPricePerGram(BigDecimal pricePerGram) { this.pricePerGram = pricePerGram; }
}
