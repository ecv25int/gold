package com.goldapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pawn_tickets")
public class PawnTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Client client;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_transaction_id")
    private Transaction loanTransaction;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redeem_transaction_id")
    private Transaction redeemTransaction;

    // removed unused goldCarat
    @Column(precision = 19, scale = 6)
    private BigDecimal pledgedGoldGrams;

    @Column(precision = 19, scale = 2)
    private BigDecimal principal;

    @Column(precision = 19, scale = 2)
    private BigDecimal interestAccrued;

    private LocalDateTime createdAt;
    // removed unused redeemedAt

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    // getters/setters omitted for brevity
}