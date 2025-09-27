package com.goldapp.controller;

import com.goldapp.entity.GoldPrice;
import com.goldapp.service.GoldPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/gold-prices")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GoldPriceController {
    
    @Autowired
    private GoldPriceService goldPriceService;
    
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentPrice() {
        Optional<GoldPrice> currentPrice = goldPriceService.getCurrentPrice();
        
        if (currentPrice.isPresent()) {
            return ResponseEntity.ok(currentPrice.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/update")
    public ResponseEntity<GoldPrice> updatePrice(@RequestParam BigDecimal buyPrice, 
                                                @RequestParam BigDecimal sellPrice) {
        GoldPrice updatedPrice = goldPriceService.updatePrice(buyPrice, sellPrice);
        return ResponseEntity.ok(updatedPrice);
    }
}