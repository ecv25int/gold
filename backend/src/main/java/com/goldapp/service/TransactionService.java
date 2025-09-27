package com.goldapp.service;

import com.goldapp.entity.GoldPrice;
import com.goldapp.entity.Transaction;
import com.goldapp.entity.User;
import com.goldapp.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionService {

    private static final int MONEY_SCALE = 2;

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final GoldPriceService goldPriceService;

    public TransactionService(TransactionRepository transactionRepository,
                              UserService userService,
                              GoldPriceService goldPriceService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.goldPriceService = goldPriceService;
    }

    public Transaction buyGold(User user, BigDecimal goldAmount) {
        validateAmount(goldAmount);
        GoldPrice currentPrice = currentPriceOrThrow();
        BigDecimal unitPrice = currentPrice.getBuyPrice();
        BigDecimal totalAmount = monetary(goldAmount.multiply(unitPrice));

        if (!userService.canAfford(user, totalAmount)) {
            throw new RuntimeException("Insufficient funds");
        }

        Transaction tx = new Transaction(
                user,
                Transaction.TransactionType.BUY,
                goldAmount,
                unitPrice,
                totalAmount
        );
        tx.setStatus(Transaction.TransactionStatus.PENDING);
        tx = transactionRepository.save(tx);

        userService.updateAccountBalance(user.getId(), totalAmount.negate());
        userService.updateGoldHoldings(user.getId(), goldAmount);

        complete(tx);
        return transactionRepository.save(tx);
    }

    public Transaction sellGold(User user, BigDecimal goldAmount) {
        validateAmount(goldAmount);
        GoldPrice currentPrice = currentPriceOrThrow();
        if (!userService.hasGoldHoldings(user, goldAmount)) {
            throw new RuntimeException("Insufficient gold holdings");
        }

        BigDecimal unitPrice = currentPrice.getSellPrice();
        BigDecimal totalAmount = monetary(goldAmount.multiply(unitPrice));

        Transaction tx = new Transaction(
                user,
                Transaction.TransactionType.SELL,
                goldAmount,
                unitPrice,
                totalAmount
        );
        tx.setStatus(Transaction.TransactionStatus.PENDING);
        tx = transactionRepository.save(tx);

        userService.updateGoldHoldings(user.getId(), goldAmount.negate());
        userService.updateAccountBalance(user.getId(), totalAmount);

        complete(tx);
        return transactionRepository.save(tx);
    }

    public Page<Transaction> getUserTransactions(User user, Pageable pageable) {
        return transactionRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    public List<Transaction> getUserTransactionsByDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(user, startDate, endDate);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));
    }

    // --- Helpers ---

    private GoldPrice currentPriceOrThrow() {
        return goldPriceService.getCurrentPrice()
                .orElseThrow(() -> new RuntimeException("Gold price not available"));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }
    }

    private void complete(Transaction tx) {
        tx.setStatus(Transaction.TransactionStatus.COMPLETED);
        tx.setCompletedAt(LocalDateTime.now());
    }

    private BigDecimal monetary(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public Transaction purchaseFromProvider(User user,
                                            Provider provider,
                                            Integer carat,
                                            BigDecimal grams,
                                            BigDecimal pricePerGram) {
        validateAmount(grams);
        Transaction tx = Transaction.purchaseFromProvider(user, provider, carat, grams, pricePerGram);
        tx.setStatus(Transaction.TransactionStatus.PENDING);
        tx = transactionRepository.save(tx);
        // inventory in
        inventoryService.record(tx, grams, carat, InventoryMovement.MovementType.IN);
        complete(tx);
        return transactionRepository.save(tx);
    }

    public Transaction sellToClient(User user,
                                    Client client,
                                    Integer carat,
                                    BigDecimal grams,
                                    BigDecimal pricePerGram) {
        validateAmount(grams);
        // (check inventory availability here)
        Transaction tx = Transaction.saleToClient(user, client, carat, grams, pricePerGram);
        tx.setStatus(Transaction.TransactionStatus.PENDING);
        tx = transactionRepository.save(tx);
        inventoryService.record(tx, grams.negate(), carat, InventoryMovement.MovementType.OUT);
        complete(tx);
        return transactionRepository.save(tx);
    }
}