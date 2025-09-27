package com.goldapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "providers")
public class Provider {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String firstName;
    
    @NotBlank
    @Size(max = 100)
    private String lastName;
    
    @NotBlank
    @Size(max = 20)
    @Column(unique = true)
    private String cedula; // Costa Rica national ID
    
    @Size(max = 100)
    @Email
    private String email;
    
    @Size(max = 20)
    private String phoneNumber;
    
    @Size(max = 200)
    private String address;
    
    @Size(max = 50)
    private String city;
    
    @Size(max = 50)
    private String province;
    
    @Size(max = 10)
    private String zipCode;
    
    @Enumerated(EnumType.STRING)
    private ProviderType providerType = ProviderType.INDIVIDUAL;
    
    @Size(max = 100)
    private String companyName; // For BUSINESS providers
    
    @Size(max = 50)
    private String taxId; // For BUSINESS providers
    
    private boolean active = true;
    
    @Column(length = 500)
    private String notes;
    
    @Size(max = 50)
    private String bankAccount; // For payments
    
    @Size(max = 100)
    private String bankName;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Provider() {}
    
    public Provider(String firstName, String lastName, String cedula) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cedula = cedula;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public ProviderType getProviderType() { return providerType; }
    public void setProviderType(ProviderType providerType) { this.providerType = providerType; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
    
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public enum ProviderType {
        INDIVIDUAL, BUSINESS
    }
}