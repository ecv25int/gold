package com.goldapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "transactions",
       indexes = {
           @Index(name = "idx_tx_user", columnList = "user_id"),
           @Index(name = "idx_tx_client", columnList = "client_id"),
           @Index(name = "idx_tx_provider", columnList = "provider_id"),
           @Index(name = "idx_tx_type_created", columnList = "type,created_at")
       })
public class Transaction {

    public static final Set<Integer> ALLOWED_CARATS = Set.of(10, 14, 18, 22, 24);
    private static final int GOLD_AMOUNT_SCALE = 6;
    private static final int MONEY_SCALE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Employee performing the transaction
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    // When selling to a client (outgoing inventory / revenue)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    // When buying from a provider (incoming inventory / cost)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private TransactionType type;

    // Nullable for legacy records if unknown; validate if present
    @Column(name = "gold_carat")
    private Integer goldCarat; // 10,14,18,22,24

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "gold_amount", precision = 19, scale = GOLD_AMOUNT_SCALE, nullable = false)
    private BigDecimal goldAmount; // grams

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "price_per_gram", precision = 19, scale = MONEY_SCALE, nullable = false)
    private BigDecimal pricePerGram;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "total_amount", precision = 19, scale = MONEY_SCALE, nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InventoryMovement> inventoryMovements;

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Invoice invoice;

    // --- Lifecycle ---------------------------------------------------------

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = TransactionStatus.PENDING;
        }
        normalizeScales();
        validateCaratIfPresent();
    }

    @PreUpdate
    protected void onUpdate() {
        normalizeScales();
        validateCaratIfPresent();
    }

    // --- Constructors ------------------------------------------------------

    public Transaction() {}

    // Full constructor (current domain)
    public Transaction(User user,
                       TransactionType type,
                       Integer goldCarat,
                       BigDecimal goldAmount,
                       BigDecimal pricePerGram,
                       BigDecimal totalAmount) {
        this.user = user;
        this.type = type;
        this.goldCarat = goldCarat;
        this.goldAmount = goldAmount;
        this.pricePerGram = pricePerGram;
        this.totalAmount = totalAmount;
    }

    // Overload kept for existing service code (goldCarat not provided)
    public Transaction(User user,
                       TransactionType type,
                       BigDecimal goldAmount,
                       BigDecimal pricePerGram,
                       BigDecimal totalAmount) {
        this(user, type, null, goldAmount, pricePerGram, totalAmount);
    }

    // Convenience factory: purchase from provider (incoming)
    public static Transaction purchaseFromProvider(User user,
                                                   Provider provider,
                                                   Integer goldCarat,
                                                   BigDecimal goldAmount,
                                                   BigDecimal pricePerGram) {
        BigDecimal total = pricePerGram.multiply(goldAmount);
        Transaction tx = new Transaction(user, TransactionType.BUY, goldCarat, goldAmount, pricePerGram, total);
        tx.setProvider(provider);
        return tx;
    }

    // Convenience factory: sale to client (outgoing)
    public static Transaction saleToClient(User user,
                                           Client client,
                                           Integer goldCarat,
                                           BigDecimal goldAmount,
                                           BigDecimal pricePerGram) {
        BigDecimal total = pricePerGram.multiply(goldAmount);
        Transaction tx = new Transaction(user, TransactionType.SELL, goldCarat, goldAmount, pricePerGram, total);
        tx.setClient(client);
        return tx;
    }

    // --- Getters / Setters -------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public Integer getGoldCarat() { return goldCarat; }
    public void setGoldCarat(Integer goldCarat) { this.goldCarat = goldCarat; }

    public BigDecimal getGoldAmount() { return goldAmount; }
    public void setGoldAmount(BigDecimal goldAmount) { this.goldAmount = goldAmount; normalizeScales(); }

    public BigDecimal getPricePerGram() { return pricePerGram; }
    public void setPricePerGram(BigDecimal pricePerGram) { this.pricePerGram = pricePerGram; normalizeScales(); }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; normalizeScales(); }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public List<InventoryMovement> getInventoryMovements() { return inventoryMovements; }
    public void setInventoryMovements(List<InventoryMovement> inventoryMovements) { this.inventoryMovements = inventoryMovements; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }

    // --- Domain helpers ----------------------------------------------------

    public void markCompleted() {
        this.status = TransactionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed(String reason) {
        this.status = TransactionStatus.FAILED;
        this.description = reason;
    }

    public void markCancelled(String reason) {
        this.status = TransactionStatus.CANCELLED;
        this.description = reason;
    }

    private void normalizeScales() {
        if (goldAmount != null) {
            goldAmount = goldAmount.setScale(GOLD_AMOUNT_SCALE, java.math.RoundingMode.HALF_UP);
        }
        if (pricePerGram != null) {
            pricePerGram = pricePerGram.setScale(MONEY_SCALE, java.math.RoundingMode.HALF_UP);
        }
        if (totalAmount != null) {
            totalAmount = totalAmount.setScale(MONEY_SCALE, java.math.RoundingMode.HALF_UP);
        }
    }

    private void validateCaratIfPresent() {
        if (goldCarat != null && !ALLOWED_CARATS.contains(goldCarat)) {
            throw new IllegalArgumentException("Unsupported gold carat: " + goldCarat);
        }
    }

    // --- Equality / HashCode (by id) ---------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // --- Enums -------------------------------------------------------------

    public enum TransactionType {
        BUY,        // from provider into inventory
        SELL,       // to client out of inventory
        PAWN_LOAN,  // client brings gold, cash out (create pawn ticket)
        PAWN_REDEEM // client pays loan + interest, gold returned
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, CANCELLED, FAILED
    }
}