package com.goldapp.repository;

import com.goldapp.entity.Transaction;
import com.goldapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Page<Transaction> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<Transaction> findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
            User user, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.status = :status ORDER BY t.createdAt DESC")
    List<Transaction> findByUserIdAndStatus(@Param("userId") Long userId, 
                                          @Param("status") Transaction.TransactionStatus status);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.status = 'COMPLETED'")
    long countCompletedTransactionsByUserAndType(@Param("user") User user, 
                                               @Param("type") Transaction.TransactionType type);
}