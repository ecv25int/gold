package com.goldapp.controller;

import com.goldapp.entity.CaratPrice;
import com.goldapp.entity.Transaction;
import com.goldapp.entity.User;
import com.goldapp.service.CaratPriceService;
import com.goldapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/carat-trade")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CaratTradeController {
    @Autowired
    private CaratPriceService caratPriceService;
    @Autowired
    private UserService userService;

    @PostMapping("/buy")
    public ResponseEntity<?> buyGoldByCarat(@AuthenticationPrincipal User user,
                                            @RequestParam int carat,
                                            @RequestParam BigDecimal grams) {
        CaratPrice price = caratPriceService.getCaratPrice(carat);
        if (price == null) return ResponseEntity.badRequest().body("Invalid carat");
        BigDecimal total = price.getPricePerGram().multiply(grams);
        if (!userService.canAfford(user, total)) {
            return ResponseEntity.badRequest().body("Insufficient funds");
        }
        userService.updateAccountBalance(user.getId(), total.negate());
        userService.updateGoldHoldings(user.getId(), grams);
        Transaction transaction = new Transaction(user, Transaction.TransactionType.BUY, grams, price.getPricePerGram(), total);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellGoldByCarat(@AuthenticationPrincipal User user,
                                             @RequestParam int carat,
                                             @RequestParam BigDecimal grams) {
        CaratPrice price = caratPriceService.getCaratPrice(carat);
        if (price == null) return ResponseEntity.badRequest().body("Invalid carat");
        if (!userService.hasGoldHoldings(user, grams)) {
            return ResponseEntity.badRequest().body("Insufficient gold holdings");
        }
        BigDecimal total = price.getPricePerGram().multiply(grams);
        userService.updateGoldHoldings(user.getId(), grams.negate());
        userService.updateAccountBalance(user.getId(), total);
        Transaction transaction = new Transaction(user, Transaction.TransactionType.SELL, grams, price.getPricePerGram(), total);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        return ResponseEntity.ok(transaction);
    }
}
