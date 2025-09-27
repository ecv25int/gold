package com.goldapp.repository;

import com.goldapp.entity.GoldInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoldInventoryRepository extends JpaRepository<GoldInventory, Long> {
    
    Optional<GoldInventory> findByCarat(Integer carat);
    
    List<GoldInventory> findAllByOrderByCaratAsc();
    
    @Query("SELECT gi FROM GoldInventory gi WHERE gi.quantityInGrams <= gi.minimumStock")
    List<GoldInventory> findLowStockItems();
    
    @Query("SELECT gi FROM GoldInventory gi WHERE gi.quantityInGrams > gi.maximumStock")
    List<GoldInventory> findOverStockItems();
    
    @Query("SELECT gi FROM GoldInventory gi WHERE gi.quantityInGrams > :minQuantity")
    List<GoldInventory> findByQuantityGreaterThan(@Param("minQuantity") BigDecimal minQuantity);
    
    @Query("SELECT SUM(gi.quantityInGrams * gi.averageBuyPrice) FROM GoldInventory gi")
    BigDecimal getTotalInventoryValue();
    
    @Query("SELECT SUM(gi.quantityInGrams) FROM GoldInventory gi")
    BigDecimal getTotalGoldQuantity();
    
    @Query("SELECT gi FROM GoldInventory gi WHERE gi.carat IN :carats")
    List<GoldInventory> findByCaratIn(@Param("carats") List<Integer> carats);
    
    boolean existsByCarat(Integer carat);
}