package com.goldapp.controller;

import com.goldapp.entity.CaratPrice;
import com.goldapp.service.CaratPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carat-prices")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CaratPriceController {
    @Autowired
    private CaratPriceService caratPriceService;

    @GetMapping("")
    public ResponseEntity<List<CaratPrice>> getAllCaratPrices() {
        return ResponseEntity.ok(caratPriceService.getAllCaratPrices());
    }

    @GetMapping("/{carat}")
    public ResponseEntity<CaratPrice> getCaratPrice(@PathVariable int carat) {
        CaratPrice price = caratPriceService.getCaratPrice(carat);
        if (price != null) {
            return ResponseEntity.ok(price);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
