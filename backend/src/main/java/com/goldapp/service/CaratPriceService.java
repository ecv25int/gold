package com.goldapp.service;

import com.goldapp.entity.CaratPrice;
import com.goldapp.repository.CaratPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaratPriceService {
    @Autowired
    private CaratPriceRepository caratPriceRepository;

    public List<CaratPrice> getAllCaratPrices() {
        return caratPriceRepository.findAllByOrderByCaratAsc();
    }

    public CaratPrice getCaratPrice(int carat) {
        return caratPriceRepository.findByCarat(carat);
    }
}
