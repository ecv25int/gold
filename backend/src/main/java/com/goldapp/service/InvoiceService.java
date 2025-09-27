package com.goldapp.service;

import com.goldapp.entity.Invoice;
import com.goldapp.entity.InvoiceItem;
import com.goldapp.entity.Transaction;
import com.goldapp.entity.Client;
import com.goldapp.entity.Provider;
import com.goldapp.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InvoiceService {
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    public Invoice createInvoice(Invoice invoice) {
        validateInvoice(invoice);
        
        // Generate invoice number if not provided
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
            invoice.setInvoiceNumber(generateInvoiceNumber());
        }
        
        // Ensure invoice number is unique
        if (invoiceRepository.existsByInvoiceNumber(invoice.getInvoiceNumber())) {
            throw new RuntimeException("Invoice number " + invoice.getInvoiceNumber() + " already exists");
        }
        
        // Calculate totals
        invoice.calculateTotals();
        
        // Set Costa Rica legal terms if not provided
        if (invoice.getLegalTerms() == null || invoice.getLegalTerms().isEmpty()) {
            invoice.setLegalTerms(getCostaRicaLegalTerms());
        }
        
        return invoiceRepository.save(invoice);
    }
    
    public Invoice createInvoiceFromTransaction(Transaction transaction) {
        Invoice.InvoiceType invoiceType = transaction.getType() == Transaction.TransactionType.SELL ?
                Invoice.InvoiceType.SALE : Invoice.InvoiceType.PURCHASE;
        
        Invoice invoice = new Invoice(transaction, invoiceType);
        
        // Set customer details based on transaction type
        if (invoiceType == Invoice.InvoiceType.SALE && transaction.getClient() != null) {
            Client client = transaction.getClient();
            invoice.setCustomerTaxId(client.getTaxId() != null ? client.getTaxId() : client.getCedula());
            invoice.setCustomerAddress(buildFullAddress(client.getAddress(), client.getCity(), client.getProvince()));
        } else if (invoiceType == Invoice.InvoiceType.PURCHASE && transaction.getProvider() != null) {
            Provider provider = transaction.getProvider();
            invoice.setCustomerTaxId(provider.getTaxId() != null ? provider.getTaxId() : provider.getCedula());
            invoice.setCustomerAddress(buildFullAddress(provider.getAddress(), provider.getCity(), provider.getProvince()));
        }
        
        // Create invoice item from transaction
        String description = String.format("Oro %dk - %.3f gramos", 
                transaction.getGoldCarat(), transaction.getGoldAmount());
        
        InvoiceItem item = new InvoiceItem(
                invoice,
                description,
                transaction.getGoldAmount(),
                transaction.getPricePerGram(),
                transaction.getGoldCarat()
        );
        
        invoice.setItems(List.of(item));
        invoice.calculateTotals();
        
        return createInvoice(invoice);
    }
    
    public Invoice updateInvoice(Long id, Invoice invoiceDetails) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + id));
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new RuntimeException("Cannot update paid invoice");
        }
        
        validateInvoice(invoiceDetails);
        
        // Check if invoice number is being changed and if new number already exists
        if (!invoice.getInvoiceNumber().equals(invoiceDetails.getInvoiceNumber()) && 
            invoiceRepository.existsByInvoiceNumber(invoiceDetails.getInvoiceNumber())) {
            throw new RuntimeException("Invoice number " + invoiceDetails.getInvoiceNumber() + " already exists");
        }
        
        // Update fields
        invoice.setInvoiceNumber(invoiceDetails.getInvoiceNumber());
        invoice.setTaxRate(invoiceDetails.getTaxRate());
        invoice.setCurrency(invoiceDetails.getCurrency());
        invoice.setNotes(invoiceDetails.getNotes());
        invoice.setLegalTerms(invoiceDetails.getLegalTerms());
        invoice.setCustomerTaxId(invoiceDetails.getCustomerTaxId());
        invoice.setCustomerAddress(invoiceDetails.getCustomerAddress());
        invoice.setPaymentMethod(invoiceDetails.getPaymentMethod());
        invoice.setDueDate(invoiceDetails.getDueDate());
        
        // Recalculate totals
        invoice.calculateTotals();
        
        return invoiceRepository.save(invoice);
    }
    
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + id));
    }
    
    public Invoice getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceNumber));
    }
    
    public List<Invoice> getInvoicesByClient(Client client) {
        return invoiceRepository.findByClient(client);
    }
    
    public List<Invoice> getInvoicesByProvider(Provider provider) {
        return invoiceRepository.findByProvider(provider);
    }
    
    public Page<Invoice> getInvoicesByClient(Client client, Pageable pageable) {
        return invoiceRepository.findByClientOrderByIssueDateDesc(client, pageable);
    }
    
    public Page<Invoice> getInvoicesByProvider(Provider provider, Pageable pageable) {
        return invoiceRepository.findByProviderOrderByIssueDateDesc(provider, pageable);
    }
    
    public List<Invoice> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        return invoiceRepository.findByStatus(status);
    }
    
    public List<Invoice> getInvoicesByType(Invoice.InvoiceType type) {
        return invoiceRepository.findByInvoiceType(type);
    }
    
    public List<Invoice> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.findByDateRange(startDate, endDate);
    }
    
    public List<Invoice> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(Invoice.InvoiceStatus.ISSUED, LocalDateTime.now());
    }
    
    public BigDecimal getTotalAmountByTypeAndStatus(Invoice.InvoiceType type, Invoice.InvoiceStatus status) {
        BigDecimal total = invoiceRepository.getTotalAmountByTypeAndStatus(type, status);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public void markInvoiceAsPaid(Long id, String paymentMethod) {
        Invoice invoice = getInvoiceById(id);
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new RuntimeException("Invoice is already paid");
        }
        
        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        invoice.setPaidDate(LocalDateTime.now());
        invoice.setPaymentMethod(paymentMethod);
        
        invoiceRepository.save(invoice);
    }
    
    public void markInvoiceAsIssued(Long id) {
        Invoice invoice = getInvoiceById(id);
        
        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw new RuntimeException("Only draft invoices can be issued");
        }
        
        invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
        invoiceRepository.save(invoice);
    }
    
    public void cancelInvoice(Long id, String reason) {
        Invoice invoice = getInvoiceById(id);
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new RuntimeException("Cannot cancel paid invoice");
        }
        
        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
        invoice.setNotes(invoice.getNotes() + "\nCANCELLED: " + reason);
        
        invoiceRepository.save(invoice);
    }
    
    public void deleteInvoice(Long id) {
        Invoice invoice = getInvoiceById(id);
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new RuntimeException("Cannot delete paid invoice");
        }
        
        invoiceRepository.deleteById(id);
    }
    
    private void validateInvoice(Invoice invoice) {
        if (invoice.getTransaction() == null) {
            throw new RuntimeException("Transaction is required");
        }
        
        if (invoice.getInvoiceType() == null) {
            throw new RuntimeException("Invoice type is required");
        }
        
        if (invoice.getTaxRate() == null || invoice.getTaxRate().compareTo(BigDecimal.ZERO) < 0 || 
            invoice.getTaxRate().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Tax rate must be between 0 and 100");
        }
        
        // Validate that invoice type matches transaction participants
        if (invoice.getInvoiceType() == Invoice.InvoiceType.SALE && invoice.getClient() == null) {
            throw new RuntimeException("Client is required for sale invoices");
        }
        
        if (invoice.getInvoiceType() == Invoice.InvoiceType.PURCHASE && invoice.getProvider() == null) {
            throw new RuntimeException("Provider is required for purchase invoices");
        }
    }
    
    private String generateInvoiceNumber() {
        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        long count = invoiceRepository.countInvoicesByDateRange(
                now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0),
                now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59)
        );
        String sequence = String.format("%06d", count + 1);
        
        return year + "-" + month + "-" + sequence;
    }
    
    private String buildFullAddress(String address, String city, String province) {
        StringBuilder fullAddress = new StringBuilder();
        
        if (address != null && !address.trim().isEmpty()) {
            fullAddress.append(address.trim());
        }
        
        if (city != null && !city.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(city.trim());
        }
        
        if (province != null && !province.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(province.trim());
        }
        
        if (fullAddress.length() > 0) {
            fullAddress.append(", Costa Rica");
        }
        
        return fullAddress.toString();
    }
    
    private String getCostaRicaLegalTerms() {
        return "Esta factura cumple con los requisitos establecidos por la Dirección General de Tributación de Costa Rica. " +
               "IVA incluido según Ley del Impuesto General sobre las Ventas. " +
               "Resolución DGT-R-48-2016 y sus reformas.";
    }
}