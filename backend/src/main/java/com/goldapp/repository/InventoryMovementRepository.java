package com.goldapp.repository;

import com.goldapp.entity.InventoryMovement;
import com.goldapp.entity.GoldInventory;
import com.goldapp.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    
    List<InventoryMovement> findByInventory(GoldInventory inventory);
    
    Page<InventoryMovement> findByInventoryOrderByCreatedAtDesc(GoldInventory inventory, Pageable pageable);
    
    List<InventoryMovement> findByTransaction(Transaction transaction);
    
    List<InventoryMovement> findByMovementType(InventoryMovement.MovementType movementType);
    
    @Query("SELECT im FROM InventoryMovement im WHERE im.createdAt BETWEEN :startDate AND :endDate ORDER BY im.createdAt DESC")
    List<InventoryMovement> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT im FROM InventoryMovement im WHERE im.inventory.carat = :carat ORDER BY im.createdAt DESC")
    List<InventoryMovement> findByGoldCarat(@Param("carat") Integer carat);
    
    @Query("SELECT im FROM InventoryMovement im WHERE im.movementType IN :types ORDER BY im.createdAt DESC")
    List<InventoryMovement> findByMovementTypes(@Param("types") List<InventoryMovement.MovementType> types);
    
    @Query("SELECT im FROM InventoryMovement im ORDER BY im.createdAt DESC")
    Page<InventoryMovement> findAllOrderByCreatedAtDesc(Pageable pageable);
}