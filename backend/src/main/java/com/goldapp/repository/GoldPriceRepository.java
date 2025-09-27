package com.goldapp.repository;

import com.goldapp.entity.GoldPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoldPriceRepository extends JpaRepository<GoldPrice, Long> {
    
    @Query("SELECT gp FROM GoldPrice gp WHERE gp.isActive = true ORDER BY gp.timestamp DESC")
    Optional<GoldPrice> findLatestActivePrice();
    
    Optional<GoldPrice> findFirstByIsActiveTrueOrderByTimestampDesc();
}