package com.goldapp.service;

import com.goldapp.entity.Client;
import com.goldapp.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    public Client createClient(Client client) {
        validateClient(client);
        
        if (clientRepository.existsByCedula(client.getCedula())) {
            throw new RuntimeException("Client with cedula " + client.getCedula() + " already exists");
        }
        
        if (client.getEmail() != null && !client.getEmail().isEmpty() && 
            clientRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Client with email " + client.getEmail() + " already exists");
        }
        
        return clientRepository.save(client);
    }
    
    public Client updateClient(Long id, Client clientDetails) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found: " + id));
        
        validateClient(clientDetails);
        
        // Check if cedula is being changed and if new cedula already exists
        if (!client.getCedula().equals(clientDetails.getCedula()) && 
            clientRepository.existsByCedula(clientDetails.getCedula())) {
            throw new RuntimeException("Client with cedula " + clientDetails.getCedula() + " already exists");
        }
        
        // Check if email is being changed and if new email already exists
        if (clientDetails.getEmail() != null && !clientDetails.getEmail().isEmpty() &&
            !clientDetails.getEmail().equals(client.getEmail()) &&
            clientRepository.existsByEmail(clientDetails.getEmail())) {
            throw new RuntimeException("Client with email " + clientDetails.getEmail() + " already exists");
        }
        
        // Update fields
        client.setFirstName(clientDetails.getFirstName());
        client.setLastName(clientDetails.getLastName());
        client.setCedula(clientDetails.getCedula());
        client.setEmail(clientDetails.getEmail());
        client.setPhoneNumber(clientDetails.getPhoneNumber());
        client.setAddress(clientDetails.getAddress());
        client.setCity(clientDetails.getCity());
        client.setProvince(clientDetails.getProvince());
        client.setZipCode(clientDetails.getZipCode());
        client.setClientType(clientDetails.getClientType());
        client.setCompanyName(clientDetails.getCompanyName());
        client.setTaxId(clientDetails.getTaxId());
        client.setNotes(clientDetails.getNotes());
        
        return clientRepository.save(client);
    }
    
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found: " + id));
    }
    
    public Optional<Client> getClientByCedula(String cedula) {
        return clientRepository.findByCedula(cedula);
    }
    
    public List<Client> getAllActiveClients() {
        return clientRepository.findByActiveTrue();
    }
    
    public Page<Client> getAllActiveClients(Pageable pageable) {
        return clientRepository.findByActiveTrue(pageable);
    }
    
    public Page<Client> searchClients(String searchTerm, Pageable pageable) {
        return clientRepository.findBySearchTerm(searchTerm, pageable);
    }
    
    public List<Client> getClientsByType(Client.ClientType clientType) {
        return clientRepository.findByClientType(clientType);
    }
    
    public List<Client> getClientsByCity(String city) {
        return clientRepository.findByCity(city);
    }
    
    public List<Client> getClientsByProvince(String province) {
        return clientRepository.findByProvince(province);
    }
    
    public void deactivateClient(Long id) {
        Client client = getClientById(id);
        client.setActive(false);
        clientRepository.save(client);
    }
    
    public void activateClient(Long id) {
        Client client = getClientById(id);
        client.setActive(true);
        clientRepository.save(client);
    }
    
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client not found: " + id);
        }
        clientRepository.deleteById(id);
    }
    
    private void validateClient(Client client) {
        if (client.getFirstName() == null || client.getFirstName().trim().isEmpty()) {
            throw new RuntimeException("First name is required");
        }
        
        if (client.getLastName() == null || client.getLastName().trim().isEmpty()) {
            throw new RuntimeException("Last name is required");
        }
        
        if (client.getCedula() == null || client.getCedula().trim().isEmpty()) {
            throw new RuntimeException("Cedula is required");
        }
        
        // Costa Rica cedula validation (basic format check)
        if (!isValidCostaRicaCedula(client.getCedula())) {
            throw new RuntimeException("Invalid Costa Rica cedula format");
        }
        
        // Business client validation
        if (client.getClientType() == Client.ClientType.BUSINESS) {
            if (client.getCompanyName() == null || client.getCompanyName().trim().isEmpty()) {
                throw new RuntimeException("Company name is required for business clients");
            }
            if (client.getTaxId() == null || client.getTaxId().trim().isEmpty()) {
                throw new RuntimeException("Tax ID is required for business clients");
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