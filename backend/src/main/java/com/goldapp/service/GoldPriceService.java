package com.goldapp.service;

import com.goldapp.entity.GoldPrice;
import com.goldapp.repository.GoldPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class GoldPriceService {
    
    @Autowired
    private GoldPriceRepository goldPriceRepository;
    
    public Optional<GoldPrice> getCurrentPrice() {
        return goldPriceRepository.findLatestActivePrice();
    }
    
    public GoldPrice updatePrice(BigDecimal buyPrice, BigDecimal sellPrice) {
        // Deactivate previous prices
        goldPriceRepository.findLatestActivePrice()
                .ifPresent(price -> {
                    price.setActive(false);
                    goldPriceRepository.save(price);
                });
        
        // Create new active price
        GoldPrice newPrice = new GoldPrice(buyPrice, sellPrice);
        return goldPriceRepository.save(newPrice);
    }
    
    // Scheduled method to update gold prices (runs every hour)
    @Scheduled(fixedRate = 3600000) // 1 hour = 3,600,000 milliseconds
    public void updateGoldPrices() {
        // In a real application, this would fetch prices from an external API
        // For now, we'll simulate price updates
        simulatePriceUpdate();
    }
    
    private void simulatePriceUpdate() {
        Optional<GoldPrice> currentPriceOpt = getCurrentPrice();
        
        BigDecimal basePrice;
        if (currentPriceOpt.isPresent()) {
            basePrice = currentPriceOpt.get().getBuyPrice();
        } else {
            basePrice = new BigDecimal("2000.00"); // Default price
        }
        
        // Simulate price fluctuation (±2%)
        double fluctuation = (Math.random() - 0.5) * 0.04; // -2% to +2%
        BigDecimal newBuyPrice = basePrice.multiply(BigDecimal.valueOf(1 + fluctuation));
        BigDecimal newSellPrice = newBuyPrice.multiply(BigDecimal.valueOf(0.98)); // 2% spread
        
        updatePrice(newBuyPrice, newSellPrice);
    }
    
    public void initializeDefaultPrice() {
        if (goldPriceRepository.findLatestActivePrice().isEmpty()) {
            updatePrice(new BigDecimal("2000.00"), new BigDecimal("1960.00"));
        }
    }
}