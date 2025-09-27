package com.goldapp.service;

import com.goldapp.entity.Provider;
import com.goldapp.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProviderService {
    
    @Autowired
    private ProviderRepository providerRepository;
    
    public Provider createProvider(Provider provider) {
        validateProvider(provider);
        
        if (providerRepository.existsByCedula(provider.getCedula())) {
            throw new RuntimeException("Provider with cedula " + provider.getCedula() + " already exists");
        }
        
        if (provider.getEmail() != null && !provider.getEmail().isEmpty() && 
            providerRepository.existsByEmail(provider.getEmail())) {
            throw new RuntimeException("Provider with email " + provider.getEmail() + " already exists");
        }
        
        return providerRepository.save(provider);
    }
    
    public Provider updateProvider(Long id, Provider providerDetails) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found: " + id));
        
        validateProvider(providerDetails);
        
        // Check if cedula is being changed and if new cedula already exists
        if (!provider.getCedula().equals(providerDetails.getCedula()) && 
            providerRepository.existsByCedula(providerDetails.getCedula())) {
            throw new RuntimeException("Provider with cedula " + providerDetails.getCedula() + " already exists");
        }
        
        // Check if email is being changed and if new email already exists
        if (providerDetails.getEmail() != null && !providerDetails.getEmail().isEmpty() &&
            !providerDetails.getEmail().equals(provider.getEmail()) &&
            providerRepository.existsByEmail(providerDetails.getEmail())) {
            throw new RuntimeException("Provider with email " + providerDetails.getEmail() + " already exists");
        }
        
        // Update fields
        provider.setFirstName(providerDetails.getFirstName());
        provider.setLastName(providerDetails.getLastName());
        provider.setCedula(providerDetails.getCedula());
        provider.setEmail(providerDetails.getEmail());
        provider.setPhoneNumber(providerDetails.getPhoneNumber());
        provider.setAddress(providerDetails.getAddress());
        provider.setCity(providerDetails.getCity());
        provider.setProvince(providerDetails.getProvince());
        provider.setZipCode(providerDetails.getZipCode());
        provider.setProviderType(providerDetails.getProviderType());
        provider.setCompanyName(providerDetails.getCompanyName());
        provider.setTaxId(providerDetails.getTaxId());
        provider.setBankAccount(providerDetails.getBankAccount());
        provider.setBankName(providerDetails.getBankName());
        provider.setNotes(providerDetails.getNotes());
        
        return providerRepository.save(provider);
    }
    
    public Provider getProviderById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found: " + id));
    }
    
    public Optional<Provider> getProviderByCedula(String cedula) {
        return providerRepository.findByCedula(cedula);
    }
    
    public List<Provider> getAllActiveProviders() {
        return providerRepository.findByActiveTrue();
    }
    
    public Page<Provider> getAllActiveProviders(Pageable pageable) {
        return providerRepository.findByActiveTrue(pageable);
    }
    
    public Page<Provider> searchProviders(String searchTerm, Pageable pageable) {
        return providerRepository.findBySearchTerm(searchTerm, pageable);
    }
    
    public List<Provider> getProvidersByType(Provider.ProviderType providerType) {
        return providerRepository.findByProviderType(providerType);
    }
    
    public List<Provider> getProvidersByCity(String city) {
        return providerRepository.findByCity(city);
    }
    
    public List<Provider> getProvidersByProvince(String province) {
        return providerRepository.findByProvince(province);
    }
    
    public void deactivateProvider(Long id) {
        Provider provider = getProviderById(id);
        provider.setActive(false);
        providerRepository.save(provider);
    }
    
    public void activateProvider(Long id) {
        Provider provider = getProviderById(id);
        provider.setActive(true);
        providerRepository.save(provider);
    }
    
    public void deleteProvider(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new RuntimeException("Provider not found: " + id);
        }
        providerRepository.deleteById(id);
    }
    
    private void validateProvider(Provider provider) {
        if (provider.getFirstName() == null || provider.getFirstName().trim().isEmpty()) {
            throw new RuntimeException("First name is required");
        }
        
        if (provider.getLastName() == null || provider.getLastName().trim().isEmpty()) {
            throw new RuntimeException("Last name is required");
        }
        
        if (provider.getCedula() == null || provider.getCedula().trim().isEmpty()) {
            throw new RuntimeException("Cedula is required");
        }
        
        // Costa Rica cedula validation (basic format check)
        if (!isValidCostaRicaCedula(provider.getCedula())) {
            throw new RuntimeException("Invalid Costa Rica cedula format");
        }
        
        // Business provider validation
        if (provider.getProviderType() == Provider.ProviderType.BUSINESS) {
            if (provider.getCompanyName() == null || provider.getCompanyName().trim().isEmpty()) {
                throw new RuntimeException("Company name is required for business providers");
            }
            if (provider.getTaxId() == null || provider.getTaxId().trim().isEmpty()) {
                throw new RuntimeException("Tax ID is required for business providers");
            }
        }
    }
    
    private boolean isValidCostaRicaCedula(String cedula) {
        // Basic Costa Rica cedula validation
        // Format: X-XXXX-XXXX where X are digits
        // More sophisticated validation can be added later
        return cedula.matches("\\d{1}-\\d{4}-\\d{4}") || cedula.matches("\\d{9}");
    }
}