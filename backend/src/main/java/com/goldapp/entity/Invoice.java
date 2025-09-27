package com.goldapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String invoiceNumber; // Costa Rica standard format
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;
    
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @NotNull
    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal taxRate = BigDecimal.valueOf(13.0); // Costa Rica IVA 13%
    
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Size(max = 10)
    private String currency = "CRC"; // Costa Rica Colones
    
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.DRAFT;
    
    @Column(length = 500)
    private String notes;
    
    @Column(length = 500)
    private String legalTerms; // Costa Rica legal requirements
    
    // Costa Rica specific fields
    @Size(max = 50)
    private String customerTaxId; // Client's tax ID
    
    @Size(max = 200)
    private String customerAddress;
    
    @Size(max = 50)
    private String paymentMethod;
    
    @Column(updatable = false)
    private LocalDateTime issueDate;
    
    private LocalDateTime dueDate;
    
    private LocalDateTime paidDate;
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceItem> items;
    
    @PrePersist
    protected void onCreate() {
        issueDate = LocalDateTime.now();
        if (dueDate == null) {
            dueDate = issueDate.plusDays(30); // Default 30 days payment term
        }
        generateInvoiceNumber();
    }
    
    private void generateInvoiceNumber() {
        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            // Costa Rica format: YYYY-MM-XXXXXX
            String year = String.valueOf(issueDate.getYear());
            String month = String.format("%02d", issueDate.getMonthValue());
            String sequence = String.format("%06d", id != null ? id : 1);
            invoiceNumber = year + "-" + month + "-" + sequence;
        }
    }
    
    public void calculateTotals() {
        if (items != null && !items.isEmpty()) {
            subtotal = items.stream()
                    .map(InvoiceItem::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        
        taxAmount = subtotal.multiply(taxRate.divide(BigDecimal.valueOf(100)));
        total = subtotal.add(taxAmount);
    }
    
    // Constructors
    public Invoice() {}
    
    public Invoice(Transaction transaction, InvoiceType invoiceType) {
        this.transaction = transaction;
        this.invoiceType = invoiceType;
        
        if (invoiceType == InvoiceType.SALE) {
            this.client = transaction.getClient();
        } else if (invoiceType == InvoiceType.PURCHASE) {
            this.provider = transaction.getProvider();
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
    
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    
    public InvoiceType getInvoiceType() { return invoiceType; }
    public void setInvoiceType(InvoiceType invoiceType) { this.invoiceType = invoiceType; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getLegalTerms() { return legalTerms; }
    public void setLegalTerms(String legalTerms) { this.legalTerms = legalTerms; }
    
    public String getCustomerTaxId() { return customerTaxId; }
    public void setCustomerTaxId(String customerTaxId) { this.customerTaxId = customerTaxId; }
    
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public LocalDateTime getIssueDate() { return issueDate; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDateTime paidDate) { this.paidDate = paidDate; }
    
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
    
    public enum InvoiceType {
        SALE, PURCHASE
    }
    
    public enum InvoiceStatus {
        DRAFT, ISSUED, PAID, CANCELLED, OVERDUE
    }
}