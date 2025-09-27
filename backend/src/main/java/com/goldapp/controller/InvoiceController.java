package com.goldapp.controller;

import com.goldapp.entity.Invoice;
import com.goldapp.entity.Client;
import com.goldapp.entity.Provider;
import com.goldapp.service.InvoiceService;
import com.goldapp.service.ClientService;
import com.goldapp.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InvoiceController {
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private ClientService clientService;
    
    @Autowired
    private ProviderService providerService;
    
    @PostMapping
    public ResponseEntity<?> createInvoice(@Valid @RequestBody Invoice invoice) {
        try {
            Invoice createdInvoice = invoiceService.createInvoice(invoice);
            return ResponseEntity.ok(createdInvoice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInvoice(@PathVariable Long id, @Valid @RequestBody Invoice invoice) {
        try {
            Invoice updatedInvoice = invoiceService.updateInvoice(id, invoice);
            return ResponseEntity.ok(updatedInvoice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoice(@PathVariable Long id) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(id);
            return ResponseEntity.ok(invoice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<?> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        try {
            Invoice invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
            return ResponseEntity.ok(invoice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getInvoicesByClient(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Client client = clientService.getClientById(clientId);
            Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
            Page<Invoice> invoices = invoiceService.getInvoicesByClient(client, pageable);
            return ResponseEntity.ok(invoices);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getInvoicesByProvider(
            @PathVariable Long providerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Provider provider = providerService.getProviderById(providerId);
            Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
            Page<Invoice> invoices = invoiceService.getInvoicesByProvider(provider, pageable);
            return ResponseEntity.ok(invoices);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Invoice>> getInvoicesByStatus(@PathVariable String status) {
        try {
            Invoice.InvoiceStatus invoiceStatus = Invoice.InvoiceStatus.valueOf(status.toUpperCase());
            List<Invoice> invoices = invoiceService.getInvoicesByStatus(invoiceStatus);
            return ResponseEntity.ok(invoices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Invoice>> getInvoicesByType(@PathVariable String type) {
        try {
            Invoice.InvoiceType invoiceType = Invoice.InvoiceType.valueOf(type.toUpperCase());
            List<Invoice> invoices = invoiceService.getInvoicesByType(invoiceType);
            return ResponseEntity.ok(invoices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<Invoice>> getInvoicesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<Invoice> invoices = invoiceService.getInvoicesByDateRange(startDate, endDate);
        return ResponseEntity.ok(invoices);
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<Invoice>> getOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceService.getOverdueInvoices();
        return ResponseEntity.ok(overdueInvoices);
    }
    
    @GetMapping("/summary")
    public ResponseEntity<?> getInvoiceSummary() {
        try {
            BigDecimal salesTotal = invoiceService.getTotalAmountByTypeAndStatus(
                    Invoice.InvoiceType.SALE, Invoice.InvoiceStatus.PAID);
            BigDecimal purchasesTotal = invoiceService.getTotalAmountByTypeAndStatus(
                    Invoice.InvoiceType.PURCHASE, Invoice.InvoiceStatus.PAID);
            BigDecimal pendingSales = invoiceService.getTotalAmountByTypeAndStatus(
                    Invoice.InvoiceType.SALE, Invoice.InvoiceStatus.ISSUED);
            
            List<Invoice> overdueInvoices = invoiceService.getOverdueInvoices();
            
            Map<String, Object> summary = Map.of(
                "totalSales", salesTotal,
                "totalPurchases", purchasesTotal,
                "pendingSales", pendingSales,
                "overdueCount", overdueInvoices.size(),
                "overdueInvoices", overdueInvoices
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/pay")
    public ResponseEntity<?> markInvoiceAsPaid(
            @PathVariable Long id,
            @RequestParam String paymentMethod) {
        try {
            invoiceService.markInvoiceAsPaid(id, paymentMethod);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/issue")
    public ResponseEntity<?> markInvoiceAsIssued(@PathVariable Long id) {
        try {
            invoiceService.markInvoiceAsIssued(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelInvoice(@PathVariable Long id, @RequestParam String reason) {
        try {
            invoiceService.cancelInvoice(id, reason);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}