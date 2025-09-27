package com.goldapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    
    @NotBlank
    @Size(max = 100)
    private String description;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(precision = 19, scale = 6)
    private BigDecimal quantity;
    
    @Size(max = 20)
    private String unit = "gramos";
    
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(precision = 19, scale = 2)
    private BigDecimal unitPrice;
    
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(precision = 19, scale = 2)
    private BigDecimal total;
    
    @Column(nullable = true)
    private Integer goldCarat; // For gold items
    
    @Size(max = 200)
    private String itemDetails;
    
    @PrePersist
    @PreUpdate
    protected void calculateTotal() {
        if (quantity != null && unitPrice != null) {
            total = quantity.multiply(unitPrice);
        }
    }
    
    // Constructors
    public InvoiceItem() {}
    
    public InvoiceItem(Invoice invoice, String description, BigDecimal quantity, 
                      BigDecimal unitPrice, Integer goldCarat) {
        this.invoice = invoice;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.goldCarat = goldCarat;
        calculateTotal();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { 
        this.quantity = quantity;
        calculateTotal();
    }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { 
        this.unitPrice = unitPrice;
        calculateTotal();
    }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public Integer getGoldCarat() { return goldCarat; }
    public void setGoldCarat(Integer goldCarat) { this.goldCarat = goldCarat; }
    
    public String getItemDetails() { return itemDetails; }
    public void setItemDetails(String itemDetails) { this.itemDetails = itemDetails; }
}