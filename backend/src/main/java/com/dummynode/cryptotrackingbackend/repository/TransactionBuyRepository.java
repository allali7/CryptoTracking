package com.dummynode.cryptotrackingbackend.repository;

import com.dummynode.cryptotrackingbackend.entity.model.TransactionBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionBuyRepository extends JpaRepository<TransactionBuy, Long> {
    @Query("SELECT tb FROM TransactionBuy tb WHERE tb.symbol = :symbol AND tb.isActive = true ORDER BY tb.createdAt ASC")
    List<TransactionBuy> findActiveOrdersBySymbol(@Param("symbol") String symbol);

    @Query("SELECT tb FROM TransactionBuy tb WHERE tb.user.userId = :userId AND tb.isActive = true")
    List<TransactionBuy> findActiveOrdersByUserId(@Param("userId") String userId);

    @Query("SELECT tb FROM TransactionBuy tb WHERE tb.symbol = :symbol AND tb.dealPrice >= :price AND tb.isActive = true ORDER BY tb.dealPrice DESC, tb.createdAt ASC")
    List<TransactionBuy> findMatchingBuyOrders(@Param("symbol") String symbol, @Param("price") BigDecimal price);
}
