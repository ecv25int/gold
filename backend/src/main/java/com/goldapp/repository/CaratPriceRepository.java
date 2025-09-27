package com.goldapp.repository;

import com.goldapp.entity.CaratPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaratPriceRepository extends JpaRepository<CaratPrice, Long> {
    List<CaratPrice> findAllByOrderByCaratAsc();
    CaratPrice findByCarat(int carat);
}
