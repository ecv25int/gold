package com.goldapp.repository;

import com.goldapp.entity.Invoice;
import com.goldapp.entity.Client;
import com.goldapp.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findByClient(Client client);
    
    List<Invoice> findByProvider(Provider provider);
    
    Page<Invoice> findByClientOrderByIssueDateDesc(Client client, Pageable pageable);
    
    Page<Invoice> findByProviderOrderByIssueDateDesc(Provider provider, Pageable pageable);
    
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    
    List<Invoice> findByInvoiceType(Invoice.InvoiceType invoiceType);
    
    @Query("SELECT i FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate ORDER BY i.issueDate DESC")
    List<Invoice> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.dueDate < :currentDate")
    List<Invoice> findOverdueInvoices(@Param("status") Invoice.InvoiceStatus status, 
                                      @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT SUM(i.total) FROM Invoice i WHERE i.invoiceType = :type AND i.status = :status")
    BigDecimal getTotalAmountByTypeAndStatus(@Param("type") Invoice.InvoiceType type, 
                                           @Param("status") Invoice.InvoiceStatus status);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate")
    Long countInvoicesByDateRange(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    boolean existsByInvoiceNumber(String invoiceNumber);
}