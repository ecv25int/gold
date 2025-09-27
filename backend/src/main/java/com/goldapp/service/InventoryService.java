package com.goldapp.service;

import com.goldapp.entity.InventoryMovement;
import com.goldapp.entity.Transaction;
import com.goldapp.repository.InventoryMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class InventoryService {

    private final InventoryMovementRepository movementRepository;

    public InventoryService(InventoryMovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    public void record(Transaction tx, BigDecimal qtyDelta, Integer carat, InventoryMovement.MovementType type) {
    InventoryMovement mv = new InventoryMovement();
    mv.setTransaction(tx);
    // InventoryMovement does not have setGoldCarat, so skip or add if needed
    mv.setQuantity(qtyDelta);
    mv.setMovementType(type);
    movementRepository.save(mv);
    }
}