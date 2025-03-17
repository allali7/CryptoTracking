package com.dummynode.cryptotrackingbackend.repository;

import com.dummynode.cryptotrackingbackend.entity.model.TransactionSell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionSellRepository extends JpaRepository<TransactionSell, Long> {
    @Query("SELECT ts FROM TransactionSell ts WHERE ts.symbol = :symbol AND ts.dealPrice <= :price AND ts.isActive = true ORDER BY ts.createdAt ASC")
    List<TransactionSell> findMatchingSellOrders(@Param("symbol") String symbol, @Param("price") BigDecimal price);

    @Query("SELECT ts FROM TransactionSell ts WHERE ts.user.userId = :userId AND ts.isActive = true")
    List<TransactionSell> findActiveOrdersByUserId(@Param("userId") String userId);
}
