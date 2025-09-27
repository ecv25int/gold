package com.goldapp.controller;

import com.goldapp.entity.Provider;
import com.goldapp.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/providers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProviderController {
    
    @Autowired
    private ProviderService providerService;
    
    @PostMapping
    public ResponseEntity<?> createProvider(@Valid @RequestBody Provider provider) {
        try {
            Provider createdProvider = providerService.createProvider(provider);
            return ResponseEntity.ok(createdProvider);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvider(@PathVariable Long id, @Valid @RequestBody Provider provider) {
        try {
            Provider updatedProvider = providerService.updateProvider(id, provider);
            return ResponseEntity.ok(updatedProvider);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProvider(@PathVariable Long id) {
        try {
            Provider provider = providerService.getProviderById(id);
            return ResponseEntity.ok(provider);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<?> getProviderByCedula(@PathVariable String cedula) {
        return providerService.getProviderByCedula(cedula)
                .map(provider -> ResponseEntity.ok(provider))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<Provider>> getAllProviders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Provider> providers = providerService.getAllActiveProviders(pageable);
        return ResponseEntity.ok(providers);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Provider>> searchProviders(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        Page<Provider> providers = providerService.searchProviders(q, pageable);
        return ResponseEntity.ok(providers);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Provider>> getProvidersByType(@PathVariable String type) {
        try {
            Provider.ProviderType providerType = Provider.ProviderType.valueOf(type.toUpperCase());
            List<Provider> providers = providerService.getProvidersByType(providerType);
            return ResponseEntity.ok(providers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Provider>> getProvidersByCity(@PathVariable String city) {
        List<Provider> providers = providerService.getProvidersByCity(city);
        return ResponseEntity.ok(providers);
    }
    
    @GetMapping("/province/{province}")
    public ResponseEntity<List<Provider>> getProvidersByProvince(@PathVariable String province) {
        List<Provider> providers = providerService.getProvidersByProvince(province);
        return ResponseEntity.ok(providers);
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateProvider(@PathVariable Long id) {
        try {
            providerService.deactivateProvider(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateProvider(@PathVariable Long id) {
        try {
            providerService.activateProvider(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvider(@PathVariable Long id) {
        try {
            providerService.deleteProvider(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}