package com.goldapp.service;

import com.goldapp.entity.User;
import com.goldapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));
        return user;
    }
    
    public User createUser(String username, String email, String rawPassword, String firstName, String lastName) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }
        
        // Note: Password should be encoded before calling this method
        // This avoids circular dependency with SecurityConfig
        User user = new User(username, email, rawPassword, firstName, lastName);
        return userRepository.save(user);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User updateAccountBalance(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        user.setAccountBalance(user.getAccountBalance().add(amount));
        return userRepository.save(user);
    }
    
    public User updateGoldHoldings(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        user.setGoldHoldings(user.getGoldHoldings().add(amount));
        return userRepository.save(user);
    }
    
    public boolean canAfford(User user, BigDecimal amount) {
        return user.getAccountBalance().compareTo(amount) >= 0;
    }
    
    public boolean hasGoldHoldings(User user, BigDecimal amount) {
        return user.getGoldHoldings().compareTo(amount) >= 0;
    }
}